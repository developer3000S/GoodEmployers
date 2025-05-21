from fastapi import APIRouter, Depends, HTTPException, status, Query, Request
from fastapi.responses import JSONResponse, HTMLResponse
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime
import json
from fastapi.templating import Jinja2Templates

from src.database import get_db
from src.models.models import Location, Client
from src.models.schemas import RouteResponse, RoutePoint, GeoJSONResponse, GeoJSONFeature, GeoJSONLineString
from src.routes.auth import get_current_client

# Router
router = APIRouter()

# Templates
templates = Jinja2Templates(directory="src/templates")

# Endpoints
@router.get("/{client_id}", response_model=GeoJSONResponse)
async def get_route_data(
    client_id: str,
    start_time: Optional[datetime] = None,
    end_time: Optional[datetime] = None,
    simplify: bool = True,
    format: str = Query("geojson", regex="^(geojson|json)$"),
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
    
    # Check permissions (only allow access to own routes unless admin)
    if current_client.id != client_id:
        # Here you would add admin check logic if needed
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to access this client's routes"
        )
    
    # Build query
    query = db.query(Location).filter(Location.client_id == client_id)
    
    if start_time:
        query = query.filter(Location.timestamp >= start_time)
    
    if end_time:
        query = query.filter(Location.timestamp <= end_time)
    
    # Order by timestamp
    query = query.order_by(Location.timestamp)
    
    # Get locations
    locations = query.all()
    
    if not locations:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No locations found for this client in the specified time range"
        )
    
    # Simplify route if requested
    if simplify and len(locations) > 100:
        # Simple downsampling for demonstration
        # In a real app, you might use Douglas-Peucker or other algorithms
        step = len(locations) // 100
        locations = locations[::step]
    
    # Extract coordinates
    coordinates = []
    for loc in locations:
        coordinates.append([loc.longitude, loc.latitude])  # [longitude, latitude]
    
    # Create GeoJSON response
    if format == "geojson":
        feature = GeoJSONFeature(
            geometry=GeoJSONLineString(coordinates=coordinates),
            properties={
                "client_id": str(client_id),
                "start_time": locations[0].timestamp.isoformat() if locations else None,
                "end_time": locations[-1].timestamp.isoformat() if locations else None,
                "point_count": len(locations)
            }
        )
        
        return GeoJSONResponse(features=[feature])
    else:
        # JSON format
        route_points = []
        for loc in locations:
            route_points.append(RoutePoint(
                latitude=loc.latitude,
                longitude=loc.longitude,
                timestamp=loc.timestamp
            ))
        
        return RouteResponse(
            client_id=client_id,
            start_time=locations[0].timestamp if locations else None,
            end_time=locations[-1].timestamp if locations else None,
            points=route_points
        )
