import { useState, useEffect } from 'react'
import { Plus, Minus, Trash2, ShoppingBag, ClipboardList, Calendar, CreditCard, Tag } from 'lucide-react'

function ClientPage({ token, cartItems, onUpdateQuantity, onRemove }) {
    const [activeTab, setActiveTab] = useState('cart')
    const [orders, setOrders] = useState([])
    const [loadingOrders, setLoadingOrders] = useState(false)
    const [stockErrors, setStockErrors] = useState({})

    useEffect(() => {
        if (!token) return;

        const fetchOrders = async () => {
            setLoadingOrders(true);
            try {
                const response = await fetch('http://localhost:8080/api/orders', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    setOrders(data || []);
                } else {
                    console.error("Не удалось загрузить историю заказов. Статус:", response.status);
                }
            } catch (error) {
                console.error("Ошибка при запросе истории заказов:", error);
            } finally {
                setLoadingOrders(false);
            }
        };

        fetchOrders();
    }, [token, activeTab]);

    const handleQtyChange = (item, newQuantity) => {
        if (newQuantity <= 0) {
            onRemove(item.id)
            setStockErrors(prev => {
                const updated = { ...prev }
                delete updated[item.productId]
                return updated
            })
            return
        }

        if (item.stock !== undefined && item.stock !== null && newQuantity > item.stock) {
            setStockErrors(prev => ({
                ...prev,
                [item.productId]: "Недостаточно на складе, выключаем можете приобрести только такое количество товара"
            }))
        } else {
            setStockErrors(prev => {
                const updated = { ...prev }
                delete updated[item.productId]
                return updated
            })
        }
        onUpdateQuantity(item.id, newQuantity)
    }

    const handleCheckout = async () => {
        try {
            setStockErrors({})
            const response = await fetch('http://localhost:8080/api/orders', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            })

            const data = await response.json().catch(() => null)

            if (response.ok) {
                setOrders(prev => [data, ...prev])
                setActiveTab('orders')
                cartItems.forEach(item => onRemove(item.id))
            } else {
                if (data && data.fieldErrors) {
                    const privateErrors = {}
                    Object.keys(data.fieldErrors).forEach(productId => {
                        privateErrors[productId] = "Недостаточно на складе"
                    })
                    setStockErrors(privateErrors)
                } else {
                    console.warn("Бэкенд отклонил превышение склада при изменении количества.");
                }
            }
        } catch (error) {
            console.error(error)
        }
    }

    const formatDate = (dateString) => {
        if (!dateString) return ''
        const date = new Date(dateString)
        return date.toLocaleDateString('ru-RU', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        })
    }

    const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)


    return (
        <div className="cart-page-container">
            <h2>Личный кабинет</h2>

            <div className="modal-tabs" style={{ marginBottom: '24px', justifyContent: 'flex-start' }}>
                <button className={`modal-tab ${activeTab === 'cart' ? 'active' : ''}`} onClick={() => setActiveTab('cart')}>
                    Моя корзина ({cartItems.reduce((sum, item) => sum + item.quantity, 0)})
                </button>
                <button className={`modal-tab ${activeTab === 'orders' ? 'active' : ''}`} onClick={() => setActiveTab('orders')}>
                    История заказов
                </button>
            </div>

            {activeTab === 'cart' && (
                cartItems.length === 0 ? (
                    <div className="empty-cart">
                        <ShoppingBag size={64} strokeWidth={1} />
                        <h2>Ваш список покупок пуст</h2>
                        <p>Воспользуйтесь каталогом или поиском в шапке, чтобы найти и добавить нужные медикаменты.</p>
                    </div>
                ) : (
                    <div className="cart-content">
                        <div className="cart-items-list">
                            {cartItems.map((item) => (
                                <div
                                    className={`cart-item-card ${stockErrors[item.productId] ? 'item-out-of-stock-border' : ''}`}
                                    key={item.id}
                                >
                                    <div className="cart-item-info">
                                        <span className="cart-item-name">{item.name}</span>
                                        <span className="cart-item-price-each">{item.price} ₸ / шт</span>

                                        {stockErrors[item.productId] && (
                                            <span className="field-error-text" style={{ display: 'block', marginTop: '4px', fontWeight: '600' }}>
                                                ⚠️ {stockErrors[item.productId]}
                                            </span>
                                        )}
                                    </div>
                                    <div className="cart-item-actions">
                                        <div className="quantity-controls">
                                            <button onClick={() => handleQtyChange(item, item.quantity - 1)}><Minus size={14} /></button>
                                            <span>{item.quantity}</span>
                                            <button onClick={() => handleQtyChange(item, item.quantity + 1)}><Plus size={14} /></button>
                                        </div>
                                        <span className="cart-item-total-price">{item.price * item.quantity} ₸</span>
                                        <button className="delete-item-btn" onClick={() => onRemove(item.id)}><Trash2 size={16} /></button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="cart-summary-panel">
                            <h3>Итого</h3>
                            <div className="summary-row">
                                <span>Количество товаров:</span>
                                <span>{cartItems.reduce((sum, item) => sum + item.quantity, 0)} шт</span>
                            </div>
                            <div className="summary-row total">
                                <span>Сумма к оплате:</span>
                                <span>{totalPrice} ₸</span>
                            </div>
                            <button className="checkout-btn" onClick={handleCheckout}>
                                Оформить заказ
                            </button>
                        </div>
                    </div>
                )
            )}

            {activeTab === 'orders' && (
                loadingOrders ? (
                    <div className="loading" style={{ textAlign: 'center', padding: '20px' }}>Загрузка истории...</div>
                ) : orders.length === 0 ? (
                    <div className="empty-cart">
                        <ClipboardList size={64} strokeWidth={1} />
                        <h2>У вас пока нет активных заказов</h2>
                        <p>После оформления покупки вся информация о статусе появится здесь.</p>
                    </div>
                ) : (
                    <div className="orders-list-container" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                        {orders.map((order) => (
                            <div className="cart-item-card" key={order.id} style={{ display: 'grid', gridTemplateColumns: '1fr auto', alignItems: 'center', padding: '20px 24px' }}>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '700', fontSize: '16px' }}>
                                        <ClipboardList size={18} className="input-icon" style={{ position: 'static', color: 'var(--primary)' }} />
                                        <span>Заказ № {order.orderNumber || order.id}</span>
                                    </div>
                                    <div style={{ display: 'flex', gap: '20px', fontSize: '14px', color: 'var(--text-muted)' }}>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                            <Calendar size={14} /> {formatDate(order.orderDate)}
                                        </span>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                            <CreditCard size={14} /> Сумма: {order.totalPrice} ₸
                                        </span>
                                    </div>
                                </div>
                                <div>
                                    <span className="stock-indicator good" style={{
                                        backgroundColor: order.status === 'CANCELLED' ? '#fef2f2' : 'var(--primary-light)',
                                        color: order.status === 'CANCELLED' ? '#ef4444' : 'var(--primary)',
                                        display: 'inline-flex',
                                        alignItems: 'center',
                                        gap: '4px'
                                    }}>
                                <Tag size={12} /> {order.status}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
        )
    )}
</div>
    )
}

export default ClientPage
