import { LayoutGrid, User, ShoppingCart } from 'lucide-react'
import SearchBar from '../SearchBar/SearchBar'

function Header({ setProducts }) {
    return (
        <header>
            <div className="container header-wrapper">
                <h1>Pharmacy</h1>

                <nav>
                    <button className="header-button catalog">
                        <LayoutGrid size={18} />
                        <span>Каталог</span>
                    </button>
                </nav>

                <SearchBar setProducts={setProducts} />

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
