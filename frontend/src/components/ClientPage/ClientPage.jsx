import { Plus, Minus, Trash2, ShoppingBag } from 'lucide-react'

function ClientPage({ cartItems = [], onUpdateQuantity, onRemove }) {

    const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)

    if (cartItems.length === 0) {
        return (
            <div className="empty-cart">
                <ShoppingBag size={64} strokeWidth={1} />
                <h2>Ваш список покупок пуст</h2>
                <p>Воспользуйтесь каталогом или поиском в шапке, чтобы найти и добавить нужные медикаменты.</p>
            </div>
        )
    }

    return (
        <div className="cart-page-container">
            <h2>Личный кабинет</h2>

            <div className="cart-content">
                <div className="cart-items-list">
                    {cartItems.map((item) => (
                        <div className="cart-item-card" key={item.id}>
                            <div className="cart-item-info">
                                <span className="cart-item-name">{item.name}</span>
                                <span className="cart-item-price-each">{item.price} ₸ / шт</span>
                            </div>

                            <div className="cart-item-actions">
                                <div className="quantity-controls">
                                    <button onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}>
                                        <Minus size={14} />
                                    </button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}>
                                        <Plus size={14} />
                                    </button>
                                </div>

                                <span className="cart-item-total-price">
                                    {item.price * item.quantity} ₸
                                </span>

                                <button className="delete-item-btn" onClick={() => onRemove(item.id)}>
                                    <Trash2 size={16} />
                                </button>
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
                    <button className="checkout-btn">
                        Оформить заказ
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ClientPage
