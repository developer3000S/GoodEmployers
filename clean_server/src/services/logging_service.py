from sqlalchemy.orm import Session
from typing import Dict, Any
import json
from src.models.models import ClientLog

def log_client_action(db: Session, client_id: str, action: str, details: Dict[str, Any] = None):
    """
    Log client actions to the database
    
    Args:
        db: Database session
        client_id: UUID of the client
        action: Action type (e.g., "register", "login", "location_submit")
        details: Additional details about the action (optional)
    """
    log_entry = ClientLog(
        client_id=client_id,
        action=action,
        details=json.dumps(details or {})  # Convert to JSON string for SQLite
    )
    
    db.add(log_entry)
    db.commit()
    
    return log_entry
