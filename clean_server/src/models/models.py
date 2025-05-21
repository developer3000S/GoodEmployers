from sqlalchemy import Column, String, Boolean, DateTime, ForeignKey, Float, BigInteger, func, Index, Double
from sqlalchemy.dialects.sqlite import BLOB
from sqlalchemy.orm import relationship
import uuid
from datetime import datetime
from src.database import Base

class Client(Base):
    __tablename__ = "clients"
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = Column(String(255), nullable=False)
    device_info = Column(String, nullable=True)  # Store JSON as string in SQLite
    created_at = Column(DateTime, default=datetime.utcnow)
    last_active = Column(DateTime, default=datetime.utcnow)
    is_active = Column(Boolean, default=True)
    
    # Relationships
    locations = relationship("Location", back_populates="client", cascade="all, delete-orphan")
    logs = relationship("ClientLog", back_populates="client", cascade="all, delete-orphan")
    refresh_tokens = relationship("RefreshToken", back_populates="client", cascade="all, delete-orphan")

class Location(Base):
    __tablename__ = "locations"
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    client_id = Column(String(36), ForeignKey("clients.id", ondelete="CASCADE"), nullable=False)
    # Store latitude and longitude as separate columns instead of using PostGIS
    latitude = Column(Double, nullable=False)
    longitude = Column(Double, nullable=False)
    accuracy = Column(Float, nullable=False)
    altitude = Column(Float, nullable=True)
    speed = Column(Float, nullable=True)
    timestamp = Column(DateTime, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    client = relationship("Client", back_populates="locations")
    
    # Create indexes for efficient querying
    __table_args__ = (
        Index('idx_locations_client_id', client_id),
        Index('idx_locations_timestamp', timestamp),
        Index('idx_locations_client_timestamp', client_id, timestamp),
    )

class ClientLog(Base):
    __tablename__ = "client_logs"
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    client_id = Column(String(36), ForeignKey("clients.id", ondelete="CASCADE"), nullable=False)
    action = Column(String(50), nullable=False)
    details = Column(String, nullable=True)  # Store JSON as string in SQLite
    timestamp = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    client = relationship("Client", back_populates="logs")
    
    # Create indexes for efficient querying
    __table_args__ = (
        Index('idx_client_logs_client_id', client_id),
        Index('idx_client_logs_action', action),
        Index('idx_client_logs_timestamp', timestamp),
        Index('idx_client_logs_client_timestamp', client_id, timestamp),
    )

class RefreshToken(Base):
    __tablename__ = "refresh_tokens"
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    client_id = Column(String(36), ForeignKey("clients.id", ondelete="CASCADE"), nullable=False)
    token = Column(String(255), nullable=False, unique=True)
    expires_at = Column(DateTime, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    client = relationship("Client", back_populates="refresh_tokens")
    
    # Create indexes for efficient querying
    __table_args__ = (
        Index('idx_refresh_tokens_token', token, unique=True),
        Index('idx_refresh_tokens_client_id', client_id),
        Index('idx_refresh_tokens_expires_at', expires_at),
    )
