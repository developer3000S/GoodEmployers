#!/bin/bash

# Server startup script for GoodEmployers
# This script initializes the database and starts the FastAPI server

# Create database tables
echo "Initializing SQLite database..."
cd /home/ubuntu/GoodEmployers/server
source venv/bin/activate

# Create a Python script to initialize the database
cat > init_db.py << EOL
from src.database import engine
from src.models.models import Base

# Create all tables
Base.metadata.create_all(bind=engine)
print("Database tables created successfully")
EOL

# Run the initialization script
python init_db.py

# Start the server
echo "Starting FastAPI server..."
uvicorn src.main:app --host 0.0.0.0 --port 8000 --reload
