#!/bin/bash

# Weblog Docker Setup Script
echo "🚀 Starting Weblog Docker Setup..."

# Create required directories
echo "📁 Creating required directories..."
mkdir -p nginx/ssl
mkdir -p logs

# Generate self-signed SSL certificates (for development only)
if [ ! -f "nginx/ssl/cert.pem" ] || [ ! -f "nginx/ssl/key.pem" ]; then
    echo "🔐 Generating self-signed SSL certificates..."
    openssl req -x509 -newkey rsa:4096 -keyout nginx/ssl/key.pem -out nginx/ssl/cert.pem -days 365 -nodes \
        -subj "/C=US/ST=State/L=City/O=Organization/OU=Unit/CN=localhost"
fi

# Start services
echo "🐳 Starting Docker containers..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Initialize MinIO bucket
echo "🗄️ Setting up MinIO bucket..."
docker-compose exec minio mc alias set myminio http://localhost:9000 minioadmin minioadmin
docker-compose exec minio mc mb myminio/weblog --ignore-existing
docker-compose exec minio mc anonymous set public myminio/weblog

# Set CORS policy for MinIO
echo "🌐 Setting up MinIO CORS..."
cat > cors.json << EOF
{
  "CORSRules": [
    {
      "AllowedOrigins": ["*"],
      "AllowedMethods": ["GET", "HEAD", "POST", "PUT", "DELETE"],
      "AllowedHeaders": ["*"],
      "ExposeHeaders": ["ETag"],
      "MaxAgeSeconds": 3000
    }
  ]
}
EOF

docker cp cors.json weblog-minio:/tmp/
docker-compose exec minio mc cors set /tmp/cors.json myminio/weblog
rm cors.json

echo "✅ Setup complete!"
echo ""
echo "🌍 Services are available at:"
echo "  Frontend: http://localhost (redirects to https://localhost)"
echo "  Backend API: http://localhost:8080"
echo "  MinIO Console: http://localhost:9001"
echo "  MySQL: localhost:3306"
echo ""
echo "📊 To check status: docker-compose ps"
echo "📝 To view logs: docker-compose logs -f"
echo "🛑 To stop: docker-compose down"