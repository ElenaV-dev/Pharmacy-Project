import { useState, useEffect, useRef } from 'react'
import { LayoutGrid, User, ShoppingCart, ArrowLeft, ChevronDown } from 'lucide-react'
import SearchBar from '../SearchBar/SearchBar'

function Header({ setProducts, selectedProductId, isCartOpen, cartCount, onLogoClick, onBackClick, onCartClick, onCategorySelect }) {
    const isProductPage = selectedProductId !== null
    const showBackButton = selectedProductId !== null || isCartOpen

    const [isMenuOpen, setIsMenuOpen] = useState(false)
    const [categories, setCategories] = useState([])
    const menuRef = useRef(null)

    useEffect(() => {
        if (isMenuOpen && categories.length === 0) {
            fetch('http://localhost:8080/api/product/categories')
                .then(res => res.json())
                .then(data => setCategories(data))
                .catch(err => console.error("Ошибка загрузки категорий:", err))
        }
    }, [isMenuOpen, categories])

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsMenuOpen(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleCategoryClick = async (categoryEnum) => {
        try {
            const response = await fetch(`http://localhost:8080/api/product/categories/${categoryEnum}`)
            const data = await response.json()
            onCategorySelect(data) // Передаем товары в App.jsx
            setIsMenuOpen(false)   // Закрываем меню
        } catch (error) {
            console.error("Ошибка загрузки товаров категории:", error)
        }
    }

    return (
        <header>
            <div className="container header-wrapper">

                <h1 className="header-logo" onClick={onLogoClick} style={{ cursor: 'pointer' }}>
                    Pharmacy
                </h1>

                {showBackButton ? (
                    <button className="header-button back-button" onClick={onBackClick}>
                        <ArrowLeft size={18} />
                        <span>Назад</span>
                    </button>
                ) : (
                    <div className="catalog-menu-container" ref={menuRef}>
                        <button
                            className={`header-button catalog ${isMenuOpen ? 'active' : ''}`}
                            onClick={() => setIsMenuOpen(!isMenuOpen)}
                        >
                            <LayoutGrid size={18} />
                            <span>Каталог</span>
                            <ChevronDown size={14} className={`chevron ${isMenuOpen ? 'rotate' : ''}`} />
                        </button>

                        {isMenuOpen && (
                            <div className="catalog-dropdown">
                                {categories.length === 0 ? (
                                    <div className="dropdown-loading">Загрузка...</div>
                                ) : (
                                    categories.map((cat) => (
                                        <button
                                            key={cat.category}
                                            className="dropdown-item"
                                            onClick={() => handleCategoryClick(cat.category)}
                                        >
                                            {cat.name}
                                        </button>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                )}

                <SearchBar setProducts={setProducts} onSearchSuccess={onBackClick} />

                <div className="user-actions">
                    <button className="header-button">
                        <User size={18} />
                        <span>Войти</span>
                    </button>

                    <button className="header-button cart-header-btn" onClick={onCartClick}>
                        <div className="cart-icon-wrapper">
                            <ShoppingCart size={18} />
                            {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                        </div>
                        <span>Корзина</span>
                    </button>
                </div>
            </div>
        </header>
    )
}

export default Header
