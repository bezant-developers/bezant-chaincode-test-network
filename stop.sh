# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

echo "Clearing environment.."
docker rm -f $(docker ps -aq)

if [ -f "docker-compose.yml" ]; then
	docker-compose -f docker-compose.yml down
fi

echo "Removing old chaincode images.."
docker rmi $(docker images | grep dev-* | awk '{print $3}')
