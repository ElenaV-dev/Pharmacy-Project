import { useState } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'
import ProductPage from './components/ProductPage/ProductPage'

function App() {
    const [products, setProducts] = useState([])
    const [selectedProductId, setSelectedProductId] = useState(null)
    const [hasSearched, setHasSearched] = useState(false)

    const handleResetToHome = () => {
        setSelectedProductId(null)
        setProducts([])
        setHasSearched(false)
    }

    const handleBackToResults = () => {
        setSelectedProductId(null)
    }

    const handleSearchSuccess = () => {
        setHasSearched(true)
        handleBackToResults()
    }

    return (
         <>
            <Header
                setProducts={setProducts}
                selectedProductId={selectedProductId}
                onLogoClick={handleResetToHome}
                onBackClick={handleSearchSuccess}
            />
            <main className="container">
                {selectedProductId !== null ? (
                    <ProductPage id={selectedProductId} />
                ) : (
                    <SearchResults
                        products={products}
                        onSelectProduct={setSelectedProductId}
                        hasSearched={hasSearched}
                    />
                )}
            </main>
         </>
    )
}

export default App
