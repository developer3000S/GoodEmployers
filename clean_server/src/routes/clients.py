from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime
import json

from src.database import get_db
from src.models.models import Client
from src.models.schemas import ClientResponse, ClientDetail, ClientUpdate, ClientsResponse
from src.routes.auth import get_current_client
from src.services.logging_service import log_client_action

# Router
router = APIRouter()

# Endpoints
@router.get("", response_model=ClientsResponse)
async def list_clients(
    active_only: bool = True,
    limit: int = Query(100, ge=1, le=1000),
    offset: int = Query(0, ge=0),
    db: Session = Depends(get_db),
    current_client: Client = Depends(get_current_client)
):
    # Build query
    query = db.query(Client)
    
    if active_only:
        query = query.filter(Client.is_active == True)
    
    # Get total count
    total_count = query.count()
    
    # Get paginated results
    clients = query.order_by(Client.last_active.desc()).offset(offset).limit(limit).all()
    
    # Convert clients for response
    client_responses = []
    for client in clients:
        client_responses.append(ClientResponse(
            id=client.id,
            name=client.name,
            created_at=client.created_at,
            last_active=client.last_active,
            is_active=client.is_active
        ))
    
    return ClientsResponse(
        total_count=total_count,
        clients=client_responses
    )

@router.get("/{client_id}", response_model=ClientDetail)
async def get_client(
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
    
    # Check permissions (only allow access to own details unless admin)
    if current_client.id != client_id:
        # Here you would add admin check logic if needed
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to access this client's details"
        )
    
    # Count locations
    location_count = db.query(Client).join(Client.locations).filter(Client.id == client_id).count()
    
    # Parse device_info JSON
    device_info = json.loads(client.device_info) if client.device_info else {}
    
    return ClientDetail(
        id=client.id,
        name=client.name,
        device_info=device_info,
        created_at=client.created_at,
        last_active=client.last_active,
        is_active=client.is_active,
        location_count=location_count
    )

@router.put("/{client_id}", response_model=dict)
async def update_client(
    client_id: str,
    client_data: ClientUpdate,
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
    
    # Check permissions (only allow updating own details)
    if current_client.id != client_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to update this client"
        )
    
    # Update fields if provided
    if client_data.name is not None:
        client.name = client_data.name
    
    if client_data.device_info is not None:
        client.device_info = json.dumps(client_data.device_info.dict())
    
    db.commit()
    db.refresh(client)
    
    # Log client update
    log_client_action(db, client.id, "client_update", {
        "updated_fields": [k for k, v in client_data.dict(exclude_unset=True).items() if v is not None]
    })
    
    return {
        "id": client.id,
        "name": client.name,
        "updated_at": datetime.now().isoformat()
    }

@router.delete("/{client_id}", response_model=dict)
async def deactivate_client(
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
    
    # Check permissions (only allow deactivating own account)
    if current_client.id != client_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to deactivate this client"
        )
    
    # Deactivate client (soft delete)
    client.is_active = False
    db.commit()
    
    # Log client deactivation
    log_client_action(db, client.id, "client_deactivate", {})
    
    return {
        "id": client.id,
        "deactivated_at": datetime.now().isoformat()
    }
