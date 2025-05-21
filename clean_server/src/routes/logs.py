from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime
import json

from src.database import get_db
from src.models.models import ClientLog, Client
from src.models.schemas import ClientLogResponse, ClientLogsResponse
from src.routes.auth import get_current_client

# Router
router = APIRouter()

# Endpoints
@router.get("/{client_id}", response_model=ClientLogsResponse)
async def get_client_logs(
    client_id: str,
    start_time: Optional[datetime] = None,
    end_time: Optional[datetime] = None,
    action: Optional[str] = None,
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
    
    # Check permissions (only allow access to own logs unless admin)
    if current_client.id != client_id:
        # Here you would add admin check logic if needed
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to access this client's logs"
        )
    
    # Build query
    query = db.query(ClientLog).filter(ClientLog.client_id == client_id)
    
    if start_time:
        query = query.filter(ClientLog.timestamp >= start_time)
    
    if end_time:
        query = query.filter(ClientLog.timestamp <= end_time)
    
    if action:
        query = query.filter(ClientLog.action == action)
    
    # Get total count
    total_count = query.count()
    
    # Get paginated results
    logs = query.order_by(ClientLog.timestamp.desc()).offset(offset).limit(limit).all()
    
    # Convert JSON strings to dictionaries for response
    log_responses = []
    for log in logs:
        details = json.loads(log.details) if log.details else None
        log_responses.append(ClientLogResponse(
            id=log.id,
            client_id=log.client_id,
            action=log.action,
            details=details,
            timestamp=log.timestamp
        ))
    
    return ClientLogsResponse(
        total_count=total_count,
        logs=log_responses
    )
