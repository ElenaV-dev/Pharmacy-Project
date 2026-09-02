import { useState } from 'react'
import './App.css'
import Header from './components/Header/Header'
import SearchResults from './components/SearchResults/SearchResults'

function App() {
    const [products, setProducts] = useState([])

    return (
         <>
            <Header setProducts={setProducts} />
            <main className="container">
                <SearchResults products={products} />
            </main>
         </>
    )
}

export default App
