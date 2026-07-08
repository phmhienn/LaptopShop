import re

def process_database_sql(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Revert COMPLETED to DELIVERED
    content = content.replace(", 'COMPLETED',", ", 'DELIVERED',")
    
    # Let's fix orders 16-25 to SHIPPING
    # They currently have 'PROCESSING'
    # Actually it's easier to find lines that have 'PROCESSING' and check if they are orders 16-25.
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if line.startswith('(') and "ORD-2024-" in line:
            # Revert the changes to order statuses exactly
            # user 2 (1-5) DELIVERED
            # user 3 (6-10) DELIVERED
            # user 4 (11-15) DELIVERED
            # user 5 (16-20) SHIPPING
            # user 6 (21-25) SHIPPING
            # user 7 (26-30) READY_TO_SHIP
            # user 8 (31-33) READY_TO_SHIP, (34-35) CONFIRMED -> READY_TO_SHIP or PENDING? Let's make CONFIRMED = PENDING since CONFIRMED is gone
            # Let's just do a regex replace on the status column
            
            # The status is the 8th parameter for order inserts:
            # (id, user_id, order_code, total_amount, discount_amount, final_amount, note, status, coupon_id, created_at)
            match = re.search(r"\((\d+),\s*\d+,\s*'ORD-2024-\d+',\s*[\d.]+,\s*[\d.]+,\s*[\d.]+,\s*(?:NULL|'[^']*'),\s*'([^']+)',", line)
            if match:
                order_id = int(match.group(1))
                old_status = match.group(2)
                new_status = old_status
                if order_id >= 16 and order_id <= 25:
                    new_status = 'SHIPPING'
                elif order_id >= 26 and order_id <= 33:
                    new_status = 'READY_TO_SHIP'
                elif order_id >= 34 and order_id <= 38: # originally CONFIRMED
                    new_status = 'PENDING'
                
                if old_status != new_status:
                    lines[i] = line.replace(f", '{old_status}',", f", '{new_status}',")

    content = '\n'.join(lines)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == "__main__":
    process_database_sql("d:/LaptopShop/Database/database.sql")
