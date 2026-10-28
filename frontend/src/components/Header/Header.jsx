import { useState, useEffect, useRef } from 'react'
import { LayoutGrid, User, ShoppingCart, ArrowLeft, ChevronDown } from 'lucide-react'
import SearchBar from '../SearchBar/SearchBar'

function Header({ setProducts, selectedProductId, isCartOpen, cartCount, token, userRole, onLogout, onLogoClick, onBackClick, onCartClick, onCategorySelect, onAuthClick, onSearchSuccess }) {

    const isPharmacist = userRole === 'PHARMACIST'
    const isAdmin = userRole === 'ADMIN'
    const showBackButton = !isPharmacist && !isAdmin && (!token ? (selectedProductId !== null || isCartOpen) : (selectedProductId !== null))
    const [isMenuOpen, setIsMenuOpen] = useState(false)
    const [categories, setCategories] = useState([])
    const menuRef = useRef(null)

    useEffect(() => {
        if (isMenuOpen && categories.length === 0) {
            fetch('/api/product/categories')
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
            const response = await fetch(`/api/product/categories/${categoryEnum}`)
            const data = await response.json()
            onCategorySelect(data)
            setIsMenuOpen(false)
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
                    !isAdmin && (
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
                                        categories.map((cat) => {
                                            const categoryKey = cat.category || cat.id || cat.name;
                                            return (
                                                <button
                                                    key={categoryKey}
                                                    className="dropdown-item"
                                                    onClick={() => handleCategoryClick(categoryKey)}
                                                >
                                                    {cat.name}
                                                </button>
                                            )
                                        })
                                    )}
                                </div>
                            )}
                        </div>
                    )
                )}

                {!isPharmacist && !isAdmin && (
                    <SearchBar setProducts={setProducts} onSearchSuccess={onSearchSuccess} />
                )}

                <div className="user-actions">
                    {token && (
                        <button className="header-button logout-btn" onClick={onLogout}>
                            <User size={18} />
                            <span>Выйти</span>
                        </button>
                    )}

                    {!token && (
                        <button className="header-button" onClick={onAuthClick}>
                            <User size={18} />
                            <span>Войти</span>
                        </button>
                    )}

                    {!isPharmacist && !isAdmin && (
                        <button className="header-button cart-header-btn" onClick={onCartClick}>
                            <div className="cart-icon-wrapper">
                                <ShoppingCart size={18} />
                                {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                            </div>
                            <span>Корзина</span>
                        </button>
                    )}
                </div>
            </div>
        </header>
    )
}

export default Header
