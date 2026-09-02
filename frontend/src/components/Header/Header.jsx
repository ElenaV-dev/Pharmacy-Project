import { LayoutGrid, User, ShoppingCart, ArrowLeft } from 'lucide-react'
import SearchBar from '../SearchBar/SearchBar'

function Header({ setProducts, selectedProductId, onLogoClick, onBackClick }) {
    const isProductPage = selectedProductId !== null

    return (
        <header>
            <div className="container header-wrapper">

                <h1 className="header-logo" onClick={onLogoClick} style={{ cursor: 'pointer' }}>
                    Pharmacy
                </h1>

                {isProductPage ? (
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

                    <button className="header-button">
                        <ShoppingCart size={18} />
                        <span>Корзина</span>
                    </button>
                </div>
            </div>
        </header>
    )
}

export default Header
