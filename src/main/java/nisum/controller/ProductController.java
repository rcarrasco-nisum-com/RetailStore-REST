package nisum.controller;

import com.wordnik.swagger.annotations.Api;
import nisum.dao.ProductDao;
import nisum.model.Product;
import nisum.restResources.ProductIn;
import nisum.restResources.ProductResource;
import nisum.restResources.ProductResources;
import nisum.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/products")
@Api(value = "products-api", description = "products api")
public class ProductController {

    @Autowired
    ProductDao productDao;

    ProductService productService;

    @Autowired
    public void setProductService(ProductService productService){
        this.productService = productService;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ProductResources getProductList() throws IOException {
        List<ProductResource> productResourceList = productService.getAll();
        addProductLinks(productResourceList);
        return new ProductResources(productResourceList);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ProductResource getProductById(@PathVariable("id") int id) throws IOException {
        ProductResource productResource = productService.findById(id);
        addProductLinks(productResource);
        return productResource;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    void saveProduct(@RequestBody ProductIn productIn
            ,HttpServletResponse response) throws IOException {

        if(productIn != null){
            int id = productService.save(new ProductResource(productIn));
            String location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .pathSegment("{id}").buildAndExpand(id)
                    .toUriString();
            response.setHeader("Location",location);
        }
    }

    @RequestMapping(value = "/",
            method = RequestMethod.PUT, produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody
    void updateProduct(@RequestBody ProductIn productIn) throws IOException {

        if(productIn != null){
            productService.update(new ProductResource(productIn));
        }
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    void deleteProductById(@PathVariable("id") int id) throws IOException {

        if(id > 0){
            if(productService.delete(productService.findById(id))) {
                String a = "";
            }
        }
    }

    private void addProductLinks(List<ProductResource> productResourceList) throws IOException {
        if (!CollectionUtils.isEmpty(productResourceList)) {
            for (ProductResource productResource : productResourceList) {
                addProductLinks(productResource);
            }
        }
    }

    private void addProductLinks(ProductResource productResource) throws IOException {
        addSelfLink(productResource);
        addProductTypeLink(productResource);
    }

    private void addSelfLink(ProductResource productResource) throws IOException {
        productResource.add(linkTo(methodOn(ProductController.class)
                .getProductById(productResource.getProductid())).withSelfRel());
    }

    private void addProductTypeLink(ProductResource productResource) throws IOException {
        productResource.add(linkTo(methodOn(ProductTypeController.class)
                .getProductTypeById(productResource.getProductTypeId())).withRel("ProducType"));
    }

}