# Render Deployment Guide
# Complete step-by-step guide to deploy your Spring Boot app to Render (FREE)

## Prerequisites
- GitHub account
- Render account (free)
- Your Spring Boot application

## Step 1: Prepare Your Code
1. Make sure your code is pushed to GitHub
2. Ensure pom.xml has PostgreSQL dependency
3. Create application-prod.properties for production config

## Step 2: Sign Up for Render
1. Go to https://render.com
2. Click "Get Started for Free"
3. Sign up with GitHub account
4. Authorize Render to access your repositories

## Step 3: Create Web Service
1. Click "New +" → "Web Service"
2. Connect your GitHub repository
3. Select your repository: razorpay-main/shopping
4. Configure the service:
   - Name: shopping-app (or any name you prefer)
   - Environment: Java
   - Build Command: mvn clean package
   - Start Command: java -jar target/Shopping-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   - Plan: Free

## Step 4: Add PostgreSQL Database
1. Click "New +" → "PostgreSQL"
2. Configure database:
   - Name: shopping-db
   - Plan: Free
   - Region: Choose closest to you
3. Click "Create Database"

## Step 5: Connect Database to Web Service
1. Go back to your web service
2. Go to "Environment" tab
3. Add environment variables:
   - DATABASE_URL: (copy from PostgreSQL service)
   - RAZORPAY_KEY: rzp_test_E1v7jL3XBuxjgO
   - RAZORPAY_SECRET: CwvaDEvYSfJuP40WfrpyrxpH
   - ADMIN_USERNAME: admin
   - ADMIN_PASSWORD: your_secure_password

## Step 6: Deploy
1. Click "Deploy" button
2. Wait for build to complete (5-10 minutes)
3. Your app will be available at: https://your-app-name.onrender.com

## Step 7: Test Your Application
1. Visit your deployed URL
2. Test seller login and product upload
3. Test user registration and shopping cart
4. Verify database persistence

## Important Notes:
- Free tier apps sleep after 15 minutes of inactivity
- Cold start takes 10-30 seconds
- Database persists data even when app sleeps
- SSL certificate is automatically provided
- Custom domain can be added later

## Troubleshooting:
- Check build logs if deployment fails
- Verify environment variables are set correctly
- Ensure DATABASE_URL is properly formatted
- Check that all dependencies are in pom.xml

## Cost: ₹0 (Completely Free!)
- Web service: Free
- PostgreSQL database: Free
- SSL certificate: Free
- Custom domain: Optional (costs extra)
