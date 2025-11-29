import React from 'react';
import ProductCard from './ProductCard';

const ProductGrid = ({ products, title }) => {
  return (
    <div className="mb-12">
      {title && <h3 className="text-2xl font-bold text-slate-800 mb-6">{title}</h3>}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {products && products.length > 0 ? (
          products.map((product, index) => (
            <ProductCard key={product.id || index} product={product} />
          ))
        ) : (
          <div className="col-span-4 text-center py-12">
            <p className="text-slate-500">No hay productos disponibles</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductGrid;