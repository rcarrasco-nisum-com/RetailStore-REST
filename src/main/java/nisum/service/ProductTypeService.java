package nisum.service;
import nisum.restResources.ProductTypeResource;

import java.util.List;

public interface ProductTypeService {

    public List<ProductTypeResource> getAll();

    public ProductTypeResource findById(int id);

    public int save(ProductTypeResource productTypeResource);

    public boolean update(ProductTypeResource productTypeResource);

    public boolean delete(ProductTypeResource productTypeResource);
}
