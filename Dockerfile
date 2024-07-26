FROM maven:3.9.8
WORKDIR /tests
COPY . .
CMD mvn clean test
