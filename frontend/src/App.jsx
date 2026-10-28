import { useState, useEffect } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'
import ProductPage from './components/ProductPage/ProductPage'
import CartPage from './components/CartPage/CartPage'
import AuthModal from './components/AuthModal/AuthModal'
import PharmacistPage from './components/PharmacistPage/PharmacistPage'
import AdminPage from './components/AdminPage/AdminPage'
import ClientPage from './components/ClientPage/ClientPage'

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
        if (!token) {
            localStorage.setItem('pharmacy_cart', JSON.stringify(cartItems))
        }
    }, [cartItems, token])

    useEffect(() => {
        if (token && userRole === 'CLIENT') {
            fetch('/api/cart', {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(res => res.ok ? res.json() : null)
            .then(userCart => {
                if (userCart) {
                    const formattedItems = userCart.items.map(item => ({
                        id: item.id || item.cartItemId,
                        productId: item.productId,
                        name: item.productName,
                        price: item.price,
                        quantity: item.quantity,
                        stock: item.stock
                    }))
                    setCartItems(formattedItems)
                }
            })
            .catch(err => console.error(err))
        }
    }, [token, userRole])

    useEffect(() => {
        if (!token) {
            fetch('/api/product', {
                method: 'GET'
            })
            .then(res => res.ok ? res.json() : [])
            .then(data => {
                setProducts(data);
            })
        .catch(err => console.error("Ошибка сброса каталога при выходе:", err));
        }
    }, [token]);

    const syncQuantityWithServer = async (cartItemId, newQuantity) => {
        if (!token || userRole !== 'CLIENT') return null
        try {
            const response = await fetch(`/api/cart/items/${cartItemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ quantity: newQuantity })
            })

            if (response.ok) {
                return await response.json()
            } else {
                const errorData = await response.json().catch(() => null)
                console.warn("Бэкенд отклонил превышение склада при изменении количества.");
                return null
            }
        } catch (error) {
            console.error("Ошибка синхронизации количества:", error)
            return null
        }
    }

    const deleteFromServer = async (cartItemId) => {
        if (!token || userRole !== 'CLIENT') return
        try {
            await fetch(`/api/cart/items/${cartItemId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            })
        } catch (error) {
            console.error("Ошибка удаления с сервера:", error)
        }
    }

    const handleAddToCart = async (product) => {
        const existingItem = cartItems.find(item => item.productId === product.id);

        if (existingItem) {
            onUpdateQuantity(existingItem.id, existingItem.quantity + 1);
            return;
        }

        if (token && userRole === 'CLIENT') {
            try {
                const response = await fetch('/api/cart/items', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        productId: product.id,
                        quantity: 1
                    })
                });

                if (response.ok) {
                    const createdItemDto = await response.json();

                    const newItem = {
                        id: createdItemDto.id,
                        productId: createdItemDto.productId,
                        name: createdItemDto.productName,
                        price: createdItemDto.price,
                        quantity: createdItemDto.quantity,
                        stock: createdItemDto.stock
                    };

                    setCartItems(prev => [...prev, newItem]);
                } else {
                    console.error("Сервер отклонил добавление товара в корзину");
                }
            } catch (error) {
                console.error("Ошибка при добавлении товара:", error);
            }
        } else {
            const newGuestItem = {
                id: Date.now(),
                productId: product.id,
                name: product.name,
                price: product.price,
                quantity: 1,
                stock: product.stock
            };
        setCartItems(prev => [...prev, newGuestItem]);
        }
    };

    const handleUpdateQuantity = async (cartItemId, newQuantity) => {
        const updatedItemFromServer = await syncQuantityWithServer(cartItemId, newQuantity)

        if (updatedItemFromServer) {
            setCartItems(prev => prev.map(item =>
                item.id === cartItemId ? {
                    ...item,
                    quantity: updatedItemFromServer.quantity,
                    stock: updatedItemFromServer.stock
                } : item
            ))
        }
    }

    const handleRemoveFromCart = (cartItemId) => {
            setCartItems(prev => prev.filter(item => item.id !== cartItemId))
            deleteFromServer(cartItemId)
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
                await fetch('/api/cart/merge', {
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
                            token={token}
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
