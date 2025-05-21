from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from jose import JWTError, jwt
from passlib.context import CryptContext
import uuid
import os
import json

from src.database import get_db
from src.models.models import Client, RefreshToken
from src.models.schemas import ClientCreate, TokenResponse, TokenRefresh
from src.services.logging_service import log_client_action

# Router
router = APIRouter()

# Security
SECRET_KEY = os.getenv("SECRET_KEY", "supersecretkey")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30
REFRESH_TOKEN_EXPIRE_DAYS = 30

# OAuth2 scheme
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/auth/login")

# Password context
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Token functions
def create_access_token(data: dict, expires_delta: timedelta = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def create_refresh_token(db: Session, client_id: str):
    # Delete any existing refresh tokens for this client
    db.query(RefreshToken).filter(RefreshToken.client_id == client_id).delete()
    
    # Create new refresh token
    token_value = str(uuid.uuid4())
    expires_at = datetime.utcnow() + timedelta(days=REFRESH_TOKEN_EXPIRE_DAYS)
    
    refresh_token = RefreshToken(
        client_id=client_id,
        token=token_value,
        expires_at=expires_at
    )
    
    db.add(refresh_token)
    db.commit()
    db.refresh(refresh_token)
    
    return token_value, REFRESH_TOKEN_EXPIRE_DAYS * 24 * 60 * 60  # Expires in seconds

# Authentication dependency
async def get_current_client(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        client_id: str = payload.get("sub")
        if client_id is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
        
    client = db.query(Client).filter(Client.id == client_id).first()
    if client is None or not client.is_active:
        raise credentials_exception
        
    return client

# Endpoints
@router.post("/register", response_model=TokenResponse, status_code=status.HTTP_201_CREATED)
async def register_client(client_data: ClientCreate, db: Session = Depends(get_db)):
    # Create new client
    client_id = str(uuid.uuid4())
    client = Client(
        id=client_id,
        name=client_data.name,
        device_info=json.dumps(client_data.device_info.dict())  # Convert to JSON string for SQLite
    )
    
    db.add(client)
    db.commit()
    db.refresh(client)
    
    # Log client registration
    log_client_action(db, client.id, "register", {"name": client.name})
    
    # Create tokens
    access_token = create_access_token({"sub": client.id})
    refresh_token, expires_in = create_refresh_token(db, client.id)
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60,
        "token_type": "bearer"
    }

@router.post("/login", response_model=TokenResponse)
async def login_client(client_id: str, db: Session = Depends(get_db)):
    # Find client
    client = db.query(Client).filter(Client.id == client_id, Client.is_active == True).first()
    if not client:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Client not found or inactive"
        )
    
    # Log client login
    log_client_action(db, client.id, "login", {})
    
    # Create tokens
    access_token = create_access_token({"sub": client.id})
    refresh_token, expires_in = create_refresh_token(db, client.id)
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60,
        "token_type": "bearer"
    }

@router.post("/refresh", response_model=TokenResponse)
async def refresh_token_endpoint(token_data: TokenRefresh, db: Session = Depends(get_db)):
    # Find refresh token
    token_record = db.query(RefreshToken).filter(
        RefreshToken.token == token_data.refresh_token,
        RefreshToken.expires_at > datetime.utcnow()
    ).first()
    
    if not token_record:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired refresh token"
        )
    
    # Check if client is active
    client = db.query(Client).filter(
        Client.id == token_record.client_id,
        Client.is_active == True
    ).first()
    
    if not client:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Client not found or inactive"
        )
    
    # Log token refresh
    log_client_action(db, client.id, "token_refresh", {})
    
    # Create new tokens
    access_token = create_access_token({"sub": client.id})
    refresh_token, expires_in = create_refresh_token(db, client.id)
    
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60,
        "token_type": "bearer"
    }
