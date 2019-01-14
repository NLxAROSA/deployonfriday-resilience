# deployonfriday-resilience
Resilience for the Deploy On Friday series

Check out this project, then build both apps using 'mvnw package' (Win) or './mvnw package'

# Deploy the apps to your favorite CF flavor.
cf push

# Map internal route to the provider application in CF.
cf map-route resilience-provider apps.internal --hostname resilience-provider

# Add a network policy to connect to the local domain
cf add-network-policy resilience-consumer --destination-app resilience-provider --protocol tcp --port 8080

# Restage both apps
cf restage resilience-provider
cf restage resilience-consumer
