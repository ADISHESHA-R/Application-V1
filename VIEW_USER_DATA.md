# How to View User Data

## Option 1: Direct Database Access (Render PostgreSQL) ✅ **EASIEST**

### Steps:
1. Go to your Render dashboard: https://dashboard.render.com
2. Click on your PostgreSQL database: **shopping-db**
3. Scroll down to **"Connections"** section
4. You'll see:
   - **Internal Database URL**
   - **External Database URL**
   - **PSQL Command**

### Method A: Using PSQL Command (Terminal)
Copy the **PSQL Command** from Render and run it in your terminal:
```bash
PGPASSWORD=YOUR_PASSWORD psql -h HOSTNAME -U shopping_user shopping_db_a0yc
```

Then run SQL queries:
```sql
-- View all users
SELECT id, username, phone_number, alternate_number, address FROM users;

-- Count total users
SELECT COUNT(*) FROM users;

-- View specific user
SELECT * FROM users WHERE username = 'your_username';

-- View users with their signup info (without password)
SELECT id, username, phone_number, address, 
       CASE WHEN photo IS NOT NULL THEN 'Has Photo' ELSE 'No Photo' END as photo_status
FROM users;
```

### Method B: Using Database GUI Tool
1. Use **pgAdmin**, **DBeaver**, or **TablePlus**
2. Connect using the **External Database URL** from Render
3. Browse the `users` table directly

---

## Option 2: Create Admin Endpoint (I can add this)

I can create a simple admin page to view users. Would you like me to:
- Create `/admin/users` endpoint to list all users?
- Add admin authentication?
- Show user details in a nice table?

---

## Option 3: Check Application Logs

User signup/login activities are logged. Check Render logs for:
- "User registered successfully"
- "User FOUND" / "User NOT FOUND"
- User IDs and usernames

---

## Quick SQL Queries for User Data

```sql
-- All users (without password)
SELECT id, username, phone_number, alternate_number, address FROM users;

-- Users with photos
SELECT id, username, phone_number FROM users WHERE photo IS NOT NULL;

-- Recent signups (if you have created_at column)
SELECT id, username, phone_number FROM users ORDER BY id DESC LIMIT 10;

-- Total user count
SELECT COUNT(*) as total_users FROM users;
```

---

## Database Connection Details

Your database details are in Render:
- **Hostname**: `dpg-d683e5a48b3s73adc4tg-a.oregon-postgres.render.com`
- **Port**: `5432`
- **Database**: `shopping_db_a0yc`
- **Username**: `shopping_user`
- **Password**: (shown in Render dashboard)

---

## Need Help?

If you want me to create an admin panel to view users through the web interface, just let me know!
