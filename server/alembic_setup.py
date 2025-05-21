import os
from alembic import command
from alembic.config import Config

# Create alembic configuration
alembic_cfg = Config("alembic.ini")

# Initialize alembic
command.init(alembic_cfg, "migrations")

# Create initial migration
command.revision(alembic_cfg, "Initial migration", autogenerate=True)

# Run migration
command.upgrade(alembic_cfg, "head")
