import { useState, useEffect } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'
import ProductPage from './components/ProductPage/ProductPage'
import CartPage from './components/CartPage/CartPage'
import AuthModal from './components/AuthModal/AuthModal' // Импортируем будущее модальное окно

function App() {
    const [products, setProducts] = useState([])
    const [selectedProductId, setSelectedProductId] = useState(null)
    const [hasSearched, setHasSearched] = useState(false)
    const [isCartOpen, setIsCartOpen] = useState(false)
    const [isAuthOpen, setIsAuthOpen] = useState(false)

    const [cartItems, setCartItems] = useState(() => {
        const savedCart = localStorage.getItem('pharmacy_cart')
        return savedCart ? JSON.parse(savedCart) : []
    })

    useEffect(() => {
        localStorage.setItem('pharmacy_cart', JSON.stringify(cartItems))
    }, [cartItems])

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

    const [token, setToken] = useState(() => localStorage.getItem('pharmacy_token') || null)

    const handleLoginSuccess = (receivedToken) => {
        localStorage.setItem('pharmacy_token', receivedToken)
        setToken(receivedToken)
        setIsAuthOpen(false)
    }

    const handleLogout = () => {
        localStorage.removeItem('pharmacy_token')
        setToken(null)
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
            />

            <main className="container">
                {isCartOpen ? (
                    <CartPage
                        cartItems={cartItems}
                        onUpdateQuantity={handleUpdateQuantity}
                        onRemove={handleRemoveFromCart}
                        onBack={handleBackToResults}
                    />
                ) : selectedProductId !== null ? (
                    <ProductPage
                        id={selectedProductId}
                        onAddToCart={handleAddToCart}
                    />
                ) : (
                    <SearchResults
                        products={products}
                        onSelectProduct={setSelectedProductId}
                        hasSearched={hasSearched}
                        onAddToCart={handleAddToCart}
                    />
                )}
            </main>

            {isAuthOpen && (
                <AuthModal
                    onClose={() => setIsAuthOpen(false)}
                    onAuthSuccess={handleLoginSuccess}
                />
            )}
         </>
    )
}

export default App
