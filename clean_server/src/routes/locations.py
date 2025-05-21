from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime
from uuid import UUID
import json

from src.database import get_db
from src.models.models import Location, Client
from src.models.schemas import LocationCreate, LocationResponse, LocationsResponse, LocationBatchCreate
from src.routes.auth import get_current_client
from src.services.logging_service import log_client_action

# Router
router = APIRouter()

# Endpoints
@router.post("", response_model=dict, status_code=status.HTTP_201_CREATED)
async def create_location(
    location: LocationCreate,
    db: Session = Depends(get_db),
    current_client: Client = Depends(get_current_client)
):
    # Create new location with separate latitude and longitude fields
    db_location = Location(
        client_id=current_client.id,
        latitude=location.latitude,
        longitude=location.longitude,
        accuracy=location.accuracy,
        altitude=location.altitude,
        speed=location.speed,
        timestamp=location.timestamp
    )
    
    db.add(db_location)
    db.commit()
    db.refresh(db_location)
    
    # Log location submission
    log_client_action(db, current_client.id, "location_submit", {
        "location_id": db_location.id,
        "timestamp": location.timestamp.isoformat()
    })
    
    return {
        "id": db_location.id,
        "received_at": datetime.now().isoformat()
    }

@router.post("/batch", response_model=dict, status_code=status.HTTP_201_CREATED)
async def create_batch_locations(
    batch: LocationBatchCreate,
    db: Session = Depends(get_db),
    current_client: Client = Depends(get_current_client)
):
    # Create locations
    db_locations = []
    for location in batch.locations:
        # Create new location with separate latitude and longitude fields
        db_location = Location(
            client_id=current_client.id,
            latitude=location.latitude,
            longitude=location.longitude,
            accuracy=location.accuracy,
            altitude=location.altitude,
            speed=location.speed,
            timestamp=location.timestamp
        )
        
        db_locations.append(db_location)
    
    db.add_all(db_locations)
    db.commit()
    
    # Log batch location submission
    log_client_action(db, current_client.id, "location_batch_submit", {
        "count": len(batch.locations),
        "timestamp": datetime.now().isoformat()
    })
    
    return {
        "received_count": len(batch.locations),
        "received_at": datetime.now().isoformat()
    }

@router.get("/{client_id}", response_model=LocationsResponse)
async def get_client_locations(
    client_id: str,
    start_time: Optional[datetime] = None,
    end_time: Optional[datetime] = None,
    limit: int = Query(100, ge=1, le=1000),
    offset: int = Query(0, ge=0),
    db: Session = Depends(get_db),
    current_client: Client = Depends(get_current_client)
):
    # Check if client exists
    client = db.query(Client).filter(Client.id == client_id).first()
    if not client:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Client not found"
        )
    
    # Check permissions (only allow access to own locations unless admin)
    if current_client.id != client_id:
        # Here you would add admin check logic if needed
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to access this client's locations"
        )
    
    # Build query
    query = db.query(Location).filter(Location.client_id == client_id)
    
    if start_time:
        query = query.filter(Location.timestamp >= start_time)
    
    if end_time:
        query = query.filter(Location.timestamp <= end_time)
    
    # Get total count
    total_count = query.count()
    
    # Get paginated results
    locations = query.order_by(Location.timestamp.desc()).offset(offset).limit(limit).all()
    
    # Convert to response model
    location_responses = []
    for loc in locations:
        location_responses.append(LocationResponse(
            id=loc.id,
            latitude=loc.latitude,
            longitude=loc.longitude,
            accuracy=loc.accuracy,
            altitude=loc.altitude,
            speed=loc.speed,
            timestamp=loc.timestamp,
            created_at=loc.created_at
        ))
    
    return LocationsResponse(
        total_count=total_count,
        locations=location_responses
    )

@router.get("/{client_id}/latest", response_model=LocationResponse)
async def get_latest_location(
    client_id: str,
    db: Session = Depends(get_db),
    current_client: Client = Depends(get_current_client)
):
    # Check if client exists
    client = db.query(Client).filter(Client.id == client_id).first()
    if not client:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Client not found"
        )
    
    # Check permissions (only allow access to own locations unless admin)
    if current_client.id != client_id:
        # Here you would add admin check logic if needed
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to access this client's locations"
        )
    
    # Get latest location
    location = db.query(Location).filter(
        Location.client_id == client_id
    ).order_by(Location.timestamp.desc()).first()
    
    if not location:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No locations found for this client"
        )
    
    return LocationResponse(
        id=location.id,
        latitude=location.latitude,
        longitude=location.longitude,
        accuracy=location.accuracy,
        altitude=location.altitude,
        speed=location.speed,
        timestamp=location.timestamp,
        created_at=location.created_at
    )
