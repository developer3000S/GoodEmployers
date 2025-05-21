from src.database import engine
from src.models.models import Base

# Create all tables
Base.metadata.create_all(bind=engine)
print("Database tables created successfully")
