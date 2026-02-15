# How to View Data in Render PostgreSQL Database

## Method 1: Using PSQL Command (Terminal) ✅ **RECOMMENDED**

### Step 1: Get Your Connection Details
From your Render dashboard, you have:
- **Hostname**: `dpg-d683e5a48b3s73adc4tg-a.oregon-postgres.render.com`
- **Port**: `5432`
- **Database**: `shopping_db_a0yc`
- **Username**: `shopping_user`
- **Password**: (Click "Show" in Render to reveal it)

### Step 2: Connect Using PSQL

**On Windows (PowerShell or Command Prompt):**

1. **Install PostgreSQL Client** (if not installed):
   - Download from: https://www.postgresql.org/download/windows/
   - Or use: `winget install PostgreSQL.PostgreSQL`

2. **Connect to Database:**
   ```bash
   psql -h dpg-d683e5a48b3s73adc4tg-a.oregon-postgres.render.com -p 5432 -U shopping_user -d shopping_db_a0yc
   ```
   
   When prompted, enter your password (from Render dashboard).

3. **Run SQL Queries:**
   ```sql
   -- View all users
   SELECT id, username, phone_number, alternate_number, address FROM users;
   
   -- View all sellers
   SELECT id, username, email, business_email, whatsapp_number FROM sellers;
   
   -- View all products
   SELECT id, name, price, category, unique_product_id FROM product;
   
   -- Count users
   SELECT COUNT(*) as total_users FROM users;
   
   -- Count sellers
   SELECT COUNT(*) as total_sellers FROM sellers;
   
   -- Count products
   SELECT COUNT(*) as total_products FROM product;
   ```

4. **Exit PSQL:**
   ```sql
   \q
   ```

---

## Method 2: Using Database GUI Tool (Easier) ✅ **BEST FOR BEGINNERS**

### Option A: DBeaver (Free, Cross-Platform)
1. Download: https://dbeaver.io/download/
2. Install and open DBeaver
3. Click "New Database Connection" → Select "PostgreSQL"
4. Enter connection details:
   - **Host**: `dpg-d683e5a48b3s73adc4tg-a.oregon-postgres.render.com`
   - **Port**: `5432`
   - **Database**: `shopping_db_a0yc`
   - **Username**: `shopping_user`
   - **Password**: (from Render dashboard)
5. Click "Test Connection" → "Finish"
6. Browse tables: `users`, `sellers`, `product`, etc.

### Option B: pgAdmin (Official PostgreSQL Tool)
1. Download: https://www.pgadmin.org/download/
2. Install and open pgAdmin
3. Right-click "Servers" → "Create" → "Server"
4. In "General" tab: Name it "Render DB"
5. In "Connection" tab:
   - **Host**: `dpg-d683e5a48b3s73adc4tg-a.oregon-postgres.render.com`
   - **Port**: `5432`
   - **Database**: `shopping_db_a0yc`
   - **Username**: `shopping_user`
   - **Password**: (from Render dashboard)
6. Click "Save"
7. Browse: Servers → Render DB → Databases → shopping_db_a0yc → Schemas → public → Tables

### Option C: TablePlus (Beautiful UI, Free for basic use)
1. Download: https://tableplus.com/
2. Click "Create a new connection" → "PostgreSQL"
3. Enter connection details (same as above)
4. Click "Connect"
5. Browse tables visually

---

## Method 3: Using Render's Built-in Query Tool

Render doesn't have a built-in query tool, but you can:
1. Use the **PSQL Command** from Render dashboard
2. Copy the command and run it in your terminal
3. It will automatically connect with the correct credentials

---

## Quick SQL Queries to View Your Data

### View All Users:
```sql
SELECT 
    id,
    username,
    phone_number,
    alternate_number,
    address,
    CASE WHEN photo IS NOT NULL THEN 'Yes' ELSE 'No' END as has_photo
FROM users
ORDER BY id DESC;
```

### View All Sellers:
```sql
SELECT 
    id,
    username,
    email,
    business_email,
    whatsapp_number,
    gst_number,
    CASE WHEN photo IS NOT NULL THEN 'Yes' ELSE 'No' END as has_photo
FROM sellers
ORDER BY id DESC;
```

### View All Products:
```sql
SELECT 
    id,
    name,
    description,
    price,
    category,
    unique_product_id,
    CASE WHEN image IS NOT NULL THEN 'Yes' ELSE 'No' END as has_image
FROM product
ORDER BY id DESC;
```

### View Products with Seller Info:
```sql
SELECT 
    p.id,
    p.name,
    p.price,
    p.category,
    s.username as seller_username
FROM product p
LEFT JOIN sellers s ON p.seller_id = s.id
ORDER BY p.id DESC;
```

---

## Important Notes:

1. **Password Security**: Never share your database password publicly
2. **Connection Limits**: Free tier has connection limits
3. **Data Persistence**: All data persists in PostgreSQL (unlike filesystem)
4. **Backup**: Consider exporting data regularly

---

## Need Help?

If you want me to create a web-based admin panel to view this data through your application, I can do that!
