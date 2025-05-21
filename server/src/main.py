import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))  # DON'T CHANGE THIS !!!

from fastapi import FastAPI, Depends, HTTPException, status, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Create FastAPI app
app = FastAPI(
    title="GoodEmployers API",
    description="API for tracking and visualizing GPS coordinates from multiple clients",
    version="1.0.0"
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with specific origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Import routes
from src.routes import auth, locations, clients, routes, logs

# Include routers
app.include_router(auth.router, prefix="/api/auth", tags=["Authentication"])
app.include_router(locations.router, prefix="/api/locations", tags=["Locations"])
app.include_router(clients.router, prefix="/api/clients", tags=["Clients"])
app.include_router(routes.router, prefix="/api/routes", tags=["Routes"])
app.include_router(logs.router, prefix="/api/logs", tags=["Logs"])

# Mount static files
app.mount("/static", StaticFiles(directory="src/static"), name="static")

# Configure templates
templates = Jinja2Templates(directory="src/templates")

# Root endpoint
@app.get("/", response_class=HTMLResponse)
async def root(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})

# Web map endpoint
@app.get("/web/map", response_class=HTMLResponse)
async def map_page(request: Request):
    return templates.TemplateResponse("map.html", {"request": request})

# Health check endpoint
@app.get("/health")
async def health_check():
    return {"status": "healthy"}
