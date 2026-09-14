import { useState, useEffect } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'
import ProductPage from './components/ProductPage/ProductPage'
import CartPage from './components/CartPage/CartPage'
import AuthModal from './components/AuthModal/AuthModal'

function App() {
    const [products, setProducts] = useState([])
    const [selectedProductId, setSelectedProductId] = useState(null)
    const [hasSearched, setHasSearched] = useState(false)
    const [isCartOpen, setIsCartOpen] = useState(false)
    const [isAuthOpen, setIsAuthOpen] = useState(false)
    const [token, setToken] = useState(() => localStorage.getItem('pharmacy_token') || null)
    const [isCheckoutWarningOpen, setIsCheckoutWarningOpen] = useState(false)
    const [authInitialMode, setAuthInitialMode] = useState('login')


    const [cartItems, setCartItems] = useState(() => {
        const savedCart = localStorage.getItem('pharmacy_cart')
        return savedCart ? JSON.parse(savedCart) : []
    })

    useEffect(() => {
        localStorage.setItem('pharmacy_cart', JSON.stringify(cartItems))
    }, [cartItems])

    useEffect(() => {
        if (token) {
            fetch('http://localhost:8080/api/cart', {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(res => {
                if (res.ok) return res.json()
                throw new Error("Не удалось загрузить корзину")
            })
            .then(userCart => {
                const formattedItems = userCart.items.map(item => ({
                    id: item.productId,
                    name: item.productName,
                    price: item.price,
                    quantity: item.quantity,
                    inStock: true
                }))
                setCartItems(formattedItems)
            })
            .catch(err => console.error(err))
        }
    }, [token])

    const handleAddToCart = (product, quantity = 1) => {
        setCartItems(prevItems => {
            const existingItem = prevItems.find(item => item.id === product.id)
            if (existingItem) {
                return prevItems.map(item =>
                    item.id === product.id ? { ...item, quantity: item.quantity + quantity } : item
                )
            }
            return [...prevItems, { ...product, quantity }]
        })
    }

    const handleUpdateQuantity = (id, newQuantity) => {
        if (newQuantity <= 0) {
            handleRemoveFromCart(id)
            return
        }
        setCartItems(prevItems =>
            prevItems.map(item => item.id === id ? { ...item, quantity: newQuantity } : item)
        )
    }

    const handleRemoveFromCart = (id) => {
        setCartItems(prevItems => prevItems.filter(item => item.id !== id))
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

    const handleLoginSuccess = async (receivedToken) => {
        localStorage.setItem('pharmacy_token', receivedToken)
        setToken(receivedToken)
        setIsAuthOpen(false)
        setHasSearched(false)

        if (cartItems.length > 0) {
            try {
                const dtoList = cartItems.map(item => ({
                    productId: item.id,
                    quantity: item.quantity
                }))
                const response = await fetch('http://localhost:8080/api/cart/merge', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${receivedToken}`
                    },
                    body: JSON.stringify({ items: dtoList })
                })
                if (response.ok) {
                    const mergedCart = await response.json()
                    const formattedItems = mergedCart.items.map(item => ({
                        id: item.productId,
                        name: item.productName,
                        price: item.price,
                        quantity: item.quantity,
                        inStock: true
                    }))
                    setCartItems(formattedItems)
                }
            } catch (error) {
                console.error(error)
            }
        }
    }

    const handleLogout = () => {
        localStorage.removeItem('pharmacy_token')
        localStorage.removeItem('pharmacy_cart')
        setToken(null)
        setCartItems([])
        setSelectedProductId(null)
        setIsCartOpen(false)
        setHasSearched(false)
    }

    return (
         <>
            <Header
                setProducts={setProducts}
                selectedProductId={selectedProductId}
                isCartOpen={isCartOpen}
                cartCount={cartItems.reduce((sum, item) => sum + item.quantity, 0)}
                token={token}
                onLogout={handleLogout}
                onLogoClick={handleResetToHome}
                onBackClick={handleBackToResults}
                onCartClick={() => {
                    setIsCartOpen(true)
                    setSelectedProductId(null)
                }}
                onCategorySelect={handleCategorySelect}
                onAuthClick={() => setIsAuthOpen(true)}
                onSearchSuccess={handleSearchSuccess} // Передаем сброс для поиска
            />

            <main className="container">
                {token ? (
                    selectedProductId !== null ? (
                        <ProductPage id={selectedProductId} onAddToCart={handleAddToCart} />
                    ) :

                    hasSearched ? (
                        <SearchResults
                            products={products}
                            onSelectProduct={setSelectedProductId}
                            hasSearched={hasSearched}
                            onAddToCart={handleAddToCart}
                        />
                    ) : (
                        <CartPage
                            cartItems={cartItems}
                            onUpdateQuantity={handleUpdateQuantity}
                            onRemove={handleRemoveFromCart}
                            onBack={null} // Передаем null, чтобы скрыть гостевые кнопки
                        />
                    )
                ) : (
                    isCartOpen ? (
                        <CartPage
                            cartItems={cartItems}
                            onUpdateQuantity={handleUpdateQuantity}
                            onRemove={handleRemoveFromCart}
                            onBack={handleBackToResults}
                            onCheckout={() => setIsCheckoutWarningOpen(true)}
                        />
                    ) : selectedProductId !== null ? (
                        <ProductPage id={selectedProductId} onAddToCart={handleAddToCart} />
                    ) : (
                        <SearchResults
                            products={products}
                            onSelectProduct={setSelectedProductId}
                            hasSearched={hasSearched}
                            onAddToCart={handleAddToCart}
                        />
                    )
                )}
            </main>

                    {isCheckoutWarningOpen && (
                        <div className="modal-overlay" onClick={() => setIsCheckoutWarningOpen(false)}>
                            <div className="modal-content text-center" onClick={(e) => e.stopPropagation()}>
                                <h3 style={{ fontSize: '20px', marginBottom: '16px', color: 'var(--text-main)' }}>
                                    Оформление заказа
                                </h3>
                                <p style={{ fontSize: '15px', color: 'var(--text-muted)', marginBottom: '24px', lineHeight: '1.5' }}>
                                    Для того, чтобы оформить заказ, необходимо Войти или Зарегистрироваться.
                                </p>
                                <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                                    <button
                                        className="auth-submit-btn"
                                        style={{ margin: 0, padding: '10px 24px' }}
                                        onClick={() => {
                                            setIsCheckoutWarningOpen(false);
                                            setAuthInitialMode('login');
                                            setIsAuthOpen(true);
                                        }}
                                    >
                                        Войти
                                    </button>
                                    <button
                                        className="header-button catalog"
                                        style={{ margin: 0, padding: '10px 24px' }}
                                        onClick={() => {
                                            setIsCheckoutWarningOpen(false);
                                            setAuthInitialMode('register');
                                            setIsAuthOpen(true);
                                        }}
                                    >
                                        Зарегистрироваться
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {isAuthOpen && (
                        <AuthModal
                            onClose={() => setIsAuthOpen(false)}
                            onAuthSuccess={handleLoginSuccess}
                            initialMode={authInitialMode}
                        />
                    )}
         </>
    )
}

export default App
