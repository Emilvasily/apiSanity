FROM nginx:latest
COPY ./target/allure-report/ /usr/share/nginx/html/