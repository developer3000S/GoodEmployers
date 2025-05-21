from flask import Flask, request, send_from_directory
from fastapi.middleware.wsgi import WSGIMiddleware
import os
import sys

# Add the parent directory to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

# Import the FastAPI app
from src.main import app as fastapi_app

# Create Flask app
flask_app = Flask(__name__)

# Mount FastAPI app under the Flask app
flask_app.wsgi_app = WSGIMiddleware(fastapi_app)

# Serve static files
@flask_app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('src/static', path)

if __name__ == '__main__':
    flask_app.run(host='0.0.0.0', port=8000)
