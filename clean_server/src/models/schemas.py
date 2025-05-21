from pydantic import BaseModel, Field
from typing import Optional, Dict, List, Any
from datetime import datetime
from uuid import UUID
import json

class DeviceInfo(BaseModel):
    model: str
    os_version: str
    app_version: str

class ClientCreate(BaseModel):
    name: str
    device_info: DeviceInfo

class ClientResponse(BaseModel):
    id: UUID
    name: str
    created_at: datetime
    last_active: datetime
    is_active: bool

class ClientDetail(ClientResponse):
    device_info: DeviceInfo
    location_count: int

class ClientUpdate(BaseModel):
    name: Optional[str] = None
    device_info: Optional[DeviceInfo] = None

class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    expires_in: int
    token_type: str = "bearer"

class TokenRefresh(BaseModel):
    refresh_token: str

class LocationCreate(BaseModel):
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)
    accuracy: float = Field(..., gt=0)
    altitude: Optional[float] = None
    speed: Optional[float] = None
    timestamp: datetime

class LocationBatchCreate(BaseModel):
    locations: List[LocationCreate]

class LocationResponse(BaseModel):
    id: int
    latitude: float
    longitude: float
    accuracy: float
    altitude: Optional[float] = None
    speed: Optional[float] = None
    timestamp: datetime
    created_at: datetime

class LocationsResponse(BaseModel):
    total_count: int
    locations: List[LocationResponse]

class ClientLogCreate(BaseModel):
    action: str
    details: Optional[Dict[str, Any]] = None

class ClientLogResponse(BaseModel):
    id: int
    client_id: UUID
    action: str
    details: Optional[Dict[str, Any]] = None
    timestamp: datetime

class ClientLogsResponse(BaseModel):
    total_count: int
    logs: List[ClientLogResponse]

class ClientsResponse(BaseModel):
    total_count: int
    clients: List[ClientResponse]

class RoutePoint(BaseModel):
    latitude: float
    longitude: float
    timestamp: datetime

class RouteResponse(BaseModel):
    client_id: UUID
    start_time: datetime
    end_time: datetime
    points: List[RoutePoint]

class GeoJSONPoint(BaseModel):
    type: str = "Point"
    coordinates: List[float]  # [longitude, latitude]

class GeoJSONLineString(BaseModel):
    type: str = "LineString"
    coordinates: List[List[float]]  # [[longitude, latitude], ...]

class GeoJSONFeature(BaseModel):
    type: str = "Feature"
    geometry: GeoJSONLineString
    properties: Dict[str, Any]

class GeoJSONResponse(BaseModel):
    type: str = "FeatureCollection"
    features: List[GeoJSONFeature]
