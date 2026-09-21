import { useState, useEffect } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'
import ProductPage from './components/ProductPage/ProductPage'
import CartPage from './components/CartPage/CartPage'
import AuthModal from './components/AuthModal/AuthModal'
import PharmacistPage from './components/PharmacistPage/PharmacistPage'
import AdminPage from './components/AdminPage/AdminPage'
import ClientPage from './components/ClientPage/ClientPage' // Импортируем новую страницу клиента

function App() {
    const [products, setProducts] = useState([])
    const [selectedProductId, setSelectedProductId] = useState(null)
    const [hasSearched, setHasSearched] = useState(false)
    const [isCartOpen, setIsCartOpen] = useState(false)
    const [isAuthOpen, setIsAuthOpen] = useState(false)
    const [isCheckoutWarningOpen, setIsCheckoutWarningOpen] = useState(false)
    const [authInitialMode, setAuthInitialMode] = useState('login')
    const [token, setToken] = useState(() => localStorage.getItem('pharmacy_token') || null)
    const [userRole, setUserRole] = useState(() => localStorage.getItem('pharmacy_role') || null)

    const [cartItems, setCartItems] = useState(() => {
        const savedCart = localStorage.getItem('pharmacy_cart')
        return savedCart ? JSON.parse(savedCart) : []
    })

    useEffect(() => {
        localStorage.setItem('pharmacy_cart', JSON.stringify(cartItems))
    }, [cartItems])

    useEffect(() => {
        if (token && userRole === 'CLIENT') {
            fetch('http://localhost:8080/api/cart', {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(res => res.ok ? res.json() : null)
            .then(userCart => {
                if (userCart) {
                    const formattedItems = userCart.items.map(item => ({
                        id: item.id || item.cartItemId,
                        productId: item.productId, // сохраняем ID товара
                        name: item.productName,
                        price: item.price,
                        quantity: item.quantity
                    }))
                    setCartItems(formattedItems)
                }
            })
        .catch(err => console.error(err))
                }
            }, [token, userRole])

    const handleAddToCart = async (product, quantity = 1) => {
        const existingItem = cartItems.find(item => item.productId === product.id)

        if (token && userRole === 'CLIENT') {
            try {
                if (existingItem) {
                    handleUpdateQuantity(existingItem.id, existingItem.quantity + quantity)
                    return
                }

    const response = await fetch('http://localhost:8080/api/cart/items', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            productId: product.id,
            quantity: quantity
        })
    })
        if (response.ok) {
            const newCartItem = await response.json()
            setCartItems(prev => [...prev, {
                id: newCartItem.id || newCartItem.cartItemId,
                productId: newCartItem.productId,
                name: newCartItem.productName,
                price: newCartItem.price,
                quantity: newCartItem.quantity
            }])
        }
    } catch (error) {
        console.error("Ошибка при добавлении товара на сервер:", error)
    }
} else {
    setCartItems(prevItems => {
        const guestItem = prevItems.find(item => item.productId === product.id)
        if (guestItem) {
            return prevItems.map(item =>
                item.productId === product.id ? { ...item, quantity: item.quantity + quantity } : item
            )
        }
        return [...prevItems, { id: product.id, productId: product.id, name: product.name, price: product.price, quantity }]
        })
    }
}

    const handleUpdateQuantity = async (id, newQuantity) => {
        if (newQuantity <= 0) {
            handleRemoveFromCart(id)
            return
        }

        setCartItems(prevItems =>
            prevItems.map(item => item.id === id ? { ...item, quantity: newQuantity } : item)
        )

        if (token && userRole === 'CLIENT') {
            try {
                await fetch(`http://localhost:8080/api/cart/items/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ quantity: newQuantity })
                })
            } catch (error) {
                console.error("Ошибка при обновлении количества:", error)
            }
        }
    }

    const handleRemoveFromCart = async (id) => {
        setCartItems(prevItems => prevItems.filter(item => item.id !== id))

        if (token && userRole === 'CLIENT') {
            try {
                await fetch(`http://localhost:8080/api/cart/items/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                })
            } catch (error) {
                console.error("Ошибка при удалении товара:", error)
            }
        }
    }

    const handleResetToHome = () => {
        setSelectedProductId(null)
        setProducts([])
        setHasSearched(false)
        setIsCartOpen(false)
    }

    const handleBackToResults = () => {
        setSelectedProductId(null)
        setIsCartOpen(false)
    }

    const handleSearchSuccess = () => {
        setHasSearched(true)
        setSelectedProductId(null)
        setIsCartOpen(false)
    }

    const handleCategorySelect = (categoryProducts) => {
        setProducts(categoryProducts)
        setHasSearched(true)
        setSelectedProductId(null)
        setIsCartOpen(false)
    }

    const handleLoginSuccess = async (receivedToken, receivedRole) => {
        localStorage.setItem('pharmacy_token', receivedToken)
        localStorage.setItem('pharmacy_role', receivedRole)
        setToken(receivedToken)
        setUserRole(receivedRole)
        setIsAuthOpen(false)
        setHasSearched(false)

        if (receivedRole === 'CLIENT' && cartItems.length > 0) {
            try {
                const dtoList = cartItems.map(item => ({
                    productId: item.id,
                    quantity: item.quantity
                }))
                await fetch('http://localhost:8080/api/cart/merge', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${receivedToken}`
                    },
                    body: JSON.stringify({ items: dtoList })
                })
                localStorage.removeItem('pharmacy_cart')
                setCartItems([])
            } catch (error) {
                console.error("Ошибка слияния корзин:", error)
            }
        }
    }

    const handleLogout = () => {
        localStorage.removeItem('pharmacy_token')
        localStorage.removeItem('pharmacy_cart')
        localStorage.removeItem('pharmacy_role')

        setToken(null)
        setUserRole(null)
        setCartItems([])
        setProducts([])

        setSelectedProductId(null)
        setHasSearched(false)
        setIsCartOpen(false)
    }

    return (
         <>
            <Header
                setProducts={setProducts}
                selectedProductId={selectedProductId}
                isCartOpen={isCartOpen}
                cartCount={cartItems.reduce((sum, item) => sum + item.quantity, 0)}
                token={token}
                userRole={userRole}
                onLogout={handleLogout}
                onLogoClick={handleResetToHome}
                onBackClick={handleBackToResults}
                onCartClick={() => {
                    setIsCartOpen(true);
                    setSelectedProductId(null);
                    setHasSearched(false);
                }}
                onCategorySelect={handleCategorySelect}
                onAuthClick={() => setIsAuthOpen(true)}
                onSearchSuccess={handleSearchSuccess}
            />

            <main className="container">
                {token ? (
                    userRole === 'ADMIN' ? (
                        <AdminPage token={token} />
                    ) :
                    userRole === 'PHARMACIST' ? (
                        <PharmacistPage token={token} products={products} setProducts={setProducts} />
                    ) :
                    selectedProductId !== null ? (
                        <ProductPage id={selectedProductId} onAddToCart={handleAddToCart} />
                    ) :
                    hasSearched ? (
                        <SearchResults products={products} onSelectProduct={setSelectedProductId} hasSearched={hasSearched} onAddToCart={handleAddToCart} />
                    ) : (
                        <ClientPage
                            cartItems={cartItems}
                            onUpdateQuantity={handleUpdateQuantity}
                            onRemove={handleRemoveFromCart}
                        />
                    )
                ) : (
                    selectedProductId !== null ? (
                        <ProductPage id={selectedProductId} onAddToCart={handleAddToCart} />
                    ) :
                    isCartOpen ? (
                        <CartPage cartItems={cartItems} onUpdateQuantity={handleUpdateQuantity} onRemove={handleRemoveFromCart} onBack={handleBackToResults} onCheckout={() => setIsCheckoutWarningOpen(true)} />
                    ) : (
                        <SearchResults products={products} onSelectProduct={setSelectedProductId} hasSearched={hasSearched} onAddToCart={handleAddToCart} />
                    )
                )}
            </main>

            {isCheckoutWarningOpen && (
                <div className="modal-overlay" onClick={() => setIsCheckoutWarningOpen(false)}>
                    <div className="modal-content text-center" onClick={(e) => e.stopPropagation()}>
                        <h3 style={{ fontSize: '20px', marginBottom: '16px', color: 'var(--text-main)' }}>Оформление заказа</h3>
                        <p style={{ fontSize: '15px', color: 'var(--text-muted)', marginBottom: '24px', lineHeight: '1.5' }}>
                            Для того, чтобы оформить заказ, необходимо Войти или Зарегистрироваться.
                        </p>
                        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                            <button className="auth-submit-btn" style={{ margin: 0, padding: '10px 24px' }} onClick={() => { setIsCheckoutWarningOpen(false); setAuthInitialMode('login'); setIsAuthOpen(true); }}>Войти</button>
                            <button className="header-button catalog" style={{ margin: 0, padding: '10px 24px' }} onClick={() => { setIsCheckoutWarningOpen(false); setAuthInitialMode('register'); setIsAuthOpen(true); }}>Зарегистрироваться</button>
                        </div>
                    </div>
                </div>
            )}

            {isAuthOpen && (
                <AuthModal onClose={() => setIsAuthOpen(false)} onAuthSuccess={handleLoginSuccess} initialMode={authInitialMode} />
            )}
         </>
    )
}

export default App
