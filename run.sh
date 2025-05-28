#!/bin/bash

# Script to build and run the Ecole Management Application

echo "Building and running Ecole Management Application..."
echo "---------------------------------------------------"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Run the application
echo "Starting the application..."
java -jar target/management-0.0.1-SNAPSHOT.jar

# This part will only execute if the application is stopped
echo "Application stopped."