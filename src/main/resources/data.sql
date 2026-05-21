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

-- 新增演示用户（密码：password123）
INSERT INTO users (username, email, password, nickname, phone, avatar, active, customer_no, created_at, updated_at) VALUES
('zhangwei', 'zhangwei@smartwallet.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张伟', '13900139001', NULL, true, 'CUST001', NOW(), NOW()),
('lina', 'lina@smartwallet.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李娜', '13900139002', NULL, true, 'CUST002', NOW(), NOW()),
('wangqiang', 'wangqiang@smartwallet.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王强', '13900139003', NULL, true, 'CUST003', NOW(), NOW());

-- 新增演示账户（user_id 1/2/3 对应张伟/李娜/王强）
INSERT INTO accounts (user_id, name, type, balance, description, active, created_at, updated_at) VALUES
(1, '工商银行储蓄卡', 'BANK_CARD', 80000.00, '主要储蓄账户', true, NOW(), NOW()),
(1, '现金', 'CASH', 5000.00, '日常现金', true, NOW(), NOW()),
(2, '建设银行储蓄卡', 'BANK_CARD', 45000.00, '主要储蓄账户', true, NOW(), NOW()),
(2, '支付宝', 'ALIPAY', 3000.00, '支付宝余额', true, NOW(), NOW()),
(3, '招商银行储蓄卡', 'BANK_CARD', 32000.00, '主要储蓄账户', true, NOW(), NOW()),
(3, '微信钱包', 'WECHAT', 1500.00, '微信零钱', true, NOW(), NOW());
