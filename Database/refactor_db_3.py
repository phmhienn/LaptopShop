import re

def process_database_sql(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    lines = content.split('\n')
    for i, line in enumerate(lines):
        # Update the INSERT INTO statement to remove status
        if line.startswith("INSERT INTO orders (id, user_id, order_code, total_amount, discount_amount, final_amount, note, status, coupon_id, created_at)"):
            lines[i] = "INSERT INTO orders (id, user_id, order_code, total_amount, discount_amount, final_amount, note, coupon_id, created_at) VALUES"
        elif line.startswith('(') and "ORD-2024-" in line:
            # We need to remove the status field which is the 8th parameter
            # e.g., (1, 2, 'ORD-2024-00001', 2398.00, 0.00, 2398.00, NULL, 'DELIVERED', NULL, '2024-01-15 10:30:00'),
            # -> (1, 2, 'ORD-2024-00001', 2398.00, 0.00, 2398.00, NULL, NULL, '2024-01-15 10:30:00'),
            # The regex will match the status value and comma.
            match = re.search(r"(\(\d+,\s*\d+,\s*'ORD-2024-\d+',\s*[\d.]+,\s*[\d.]+,\s*[\d.]+,\s*(?:NULL|'[^']*')),\s*'[^']+',(.*)", line)
            if match:
                lines[i] = match.group(1) + "," + match.group(2)

    content = '\n'.join(lines)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == "__main__":
    process_database_sql("d:/LaptopShop/Database/database.sql")
