import { LayoutGrid, User, ShoppingCart, ArrowLeft } from 'lucide-react'
import SearchBar from '../SearchBar/SearchBar'

function Header({ setProducts, selectedProductId, isCartOpen, cartCount, onLogoClick, onBackClick, onCartClick }) {
    const showBackButton = selectedProductId !== null || isCartOpen

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
                    <nav>
                        <button className="header-button catalog">
                            <LayoutGrid size={18} />
                            <span>Каталог</span>
                        </button>
                    </nav>
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
