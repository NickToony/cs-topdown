# Remove existing build
rm -r dist
mkdir -p dist

# Build all flavours
docker build -t cstopdown . 

# Create a dummy container layer to pull files from
docker create --name dummy cstopdown
docker cp dummy:/app/html/build/dist ./dist/html
docker cp dummy:/app/desktop/build/libs ./dist/desktop
docker cp dummy:/app/server/build/libs ./dist/server
# Cleanup the dummy layer
docker rm -f dummy

# Cleanup the build image
# docker image rm cstopdown

# docker build -f Dockerfile.server -t cstopdown:server-latest .
# docker image tag cstopdown:server-latest nicktoony/cstopdown:server-latest
# docker image push nicktoony/cstopdown:server-latest
# docker image rm cstopdown:server-latest

# docker run --rm -it --entrypoint bash cstopdown