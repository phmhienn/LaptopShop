import re

def process_database_sql(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # We need to extract shipping_name, shipping_phone, shipping_address from orders 
    # and put them into shipments. Also change order statuses.
    # Replace the INSERT INTO orders line
    old_order_insert = "INSERT INTO orders (id, user_id, order_code, total_amount, discount_amount, final_amount, shipping_name, shipping_phone, shipping_address, note, status, coupon_id, created_at) VALUES"
    new_order_insert = "INSERT INTO orders (id, user_id, order_code, total_amount, discount_amount, final_amount, note, status, coupon_id, created_at) VALUES"
    
    if old_order_insert in content:
        content = content.replace(old_order_insert, new_order_insert)

    # Process the order values. A typical line:
    # (1,  2, 'ORD-2024-00001', 2398.00, 0.00,    2398.00, 'Nguyen Van An',  '0901000001', '123 Le Loi, Ben Thanh, Quan 1, Ho Chi Minh',              NULL,                        'DELIVERED',  NULL, '2024-01-15 10:30:00'),
    
    order_pattern = re.compile(r"\((\d+),\s*(\d+),\s*('[^']+'),\s*([\d.]+),\s*([\d.]+),\s*([\d.]+),\s*('[^']+'),\s*('[^']+'),\s*('[^']+'),\s*(NULL|'[^']*'),\s*'([^']+)',\s*(NULL|\d+),\s*('[^']+')\)")
    
    shipment_inserts = []
    
    def replace_order(match):
        order_id = match.group(1)
        user_id = match.group(2)
        order_code = match.group(3)
        total_amount = match.group(4)
        discount_amount = match.group(5)
        final_amount = match.group(6)
        shipping_name = match.group(7)
        shipping_phone = match.group(8)
        shipping_address = match.group(9)
        note = match.group(10)
        status = match.group(11)
        coupon_id = match.group(12)
        created_at = match.group(13)
        
        # Determine Shipment Status based on old order status
        shipment_status = 'PENDING'
        new_order_status = status
        
        if status == 'DELIVERED':
            new_order_status = 'COMPLETED'
            shipment_status = 'DELIVERED'
        elif status == 'SHIPPING':
            new_order_status = 'PROCESSING'
            shipment_status = 'SHIPPING'
        elif status == 'PROCESSING':
            new_order_status = 'PROCESSING'
            shipment_status = 'READY_TO_SHIP'
        elif status == 'CONFIRMED':
            new_order_status = 'CONFIRMED'
            shipment_status = 'PENDING'
        elif status == 'PENDING':
            new_order_status = 'PENDING'
            shipment_status = 'PENDING'
        elif status == 'CANCELLED':
            new_order_status = 'CANCELLED'
            shipment_status = 'FAILED'
        elif status == 'RETURNED':
            new_order_status = 'COMPLETED'
            shipment_status = 'RETURNED'
            
        # Create shipment insert statement
        # shipments (id, order_id, receiver_name, receiver_phone, receiver_address, tracking_number, shipping_provider, shipping_fee, status, shipped_at, delivered_at, created_at, updated_at)
        shipped_at = created_at if shipment_status in ['SHIPPING', 'DELIVERED', 'RETURNED'] else 'NULL'
        delivered_at = created_at if shipment_status in ['DELIVERED', 'RETURNED'] else 'NULL'
        
        shipment_insert = f"({order_id}, {order_id}, {shipping_name}, {shipping_phone}, {shipping_address}, 'TRK-{order_id}', 'FastDelivery', 0.00, '{shipment_status}', {shipped_at}, {delivered_at}, {created_at}, {created_at})"
        shipment_inserts.append(shipment_insert)
        
        return f"({order_id}, {user_id}, {order_code}, {total_amount}, {discount_amount}, {final_amount}, {note}, '{new_order_status}', {coupon_id}, {created_at})"

    content = order_pattern.sub(replace_order, content)
    
    # Replace old shipments inserts if they exist
    old_shipments_insert_start = content.find("INSERT INTO shipments")
    if old_shipments_insert_start != -1:
        # Find the end of the shipments insert block
        old_shipments_insert_end = content.find(";", old_shipments_insert_start)
        if old_shipments_insert_end != -1:
            content = content[:old_shipments_insert_start] + content[old_shipments_insert_end + 1:]

    # Add new shipments inserts
    if shipment_inserts:
        shipment_sql = "\n\n-- ----------------------------------------------------------------\n-- Shipments\n-- ----------------------------------------------------------------\n"
        shipment_sql += "INSERT INTO shipments (id, order_id, receiver_name, receiver_phone, receiver_address, tracking_number, shipping_provider, shipping_fee, status, shipped_at, delivered_at, created_at, updated_at) VALUES\n"
        shipment_sql += ",\n".join(shipment_inserts) + ";\n"
        
        # Append before reviews
        reviews_index = content.find("-- ----------------------------------------------------------------\n-- Reviews")
        if reviews_index != -1:
            content = content[:reviews_index] + shipment_sql + "\n" + content[reviews_index:]
        else:
            content += shipment_sql
            
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == "__main__":
    process_database_sql("d:/LaptopShop/Database/database.sql")
