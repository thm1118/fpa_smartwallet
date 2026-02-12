-- Insert default categories
INSERT INTO categories (name, type, icon, color, is_system) VALUES
('Salary', 'INCOME', 'money', '#4CAF50', true),
('Bonus', 'INCOME', 'gift', '#66BB6A', true),
('Investment', 'INCOME', 'trending-up', '#81C784', true),
('Food', 'EXPENSE', 'restaurant', '#F44336', true),
('Transportation', 'EXPENSE', 'car', '#E57373', true),
('Shopping', 'EXPENSE', 'shopping-cart', '#EF5350', true),
('Entertainment', 'EXPENSE', 'music', '#FF7043', true),
('Healthcare', 'EXPENSE', 'heart', '#EC407A', true),
('Education', 'EXPENSE', 'book', '#AB47BC', true),
('Housing', 'EXPENSE', 'home', '#5C6BC0', true),
('Utilities', 'EXPENSE', 'flash', '#42A5F5', true),
('Other', 'EXPENSE', 'more', '#78909C', true);
