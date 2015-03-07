package nisum.controller;

import com.wordnik.swagger.annotations.Api;
import nisum.restResources.ProductTypeIn;
import nisum.restResources.ProductTypeResource;
import nisum.restResources.ProductTypeResources;
import nisum.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/productTypes")
@Api(value = "productTypes-api", description = "products api")
public class ProductTypeController {

    ProductTypeService productTypeService;

    @Autowired
    public void setProductService(ProductTypeService productTypeService){
        this.productTypeService = productTypeService;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ProductTypeResources getProductTypeList() throws IOException {
        List<ProductTypeResource> productTypeResourceList = productTypeService.getAll();
        addProductTypeLinks(productTypeResourceList);

        return new ProductTypeResources(productTypeResourceList);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ProductTypeResource getProductTypeById(@PathVariable("id") int id) throws IOException {

        ProductTypeResource productTypeResource = productTypeService.findById(id);
        addProductTypeLinks(productTypeResource);
        return productTypeResource;
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleException(Exception e) {
        return e.toString();
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    void saveProductType(@RequestBody ProductTypeIn productTypeIn
            ,HttpServletResponse response) throws IOException {

        if(productTypeIn != null){
            int id = productTypeService.save(new ProductTypeResource(productTypeIn));
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
    void updateProductType(@RequestBody ProductTypeIn productTypeIn) throws IOException {

        if(productTypeIn != null){
            productTypeService.update(new ProductTypeResource(productTypeIn));
        }
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    void deleteProductTypeById(@PathVariable("id") int id) throws IOException {

        if(id > 0){
            if(productTypeService.delete(productTypeService.findById(id))) {
                String a = "";
            }
        }
    }

    private void addProductTypeLinks(List<ProductTypeResource> productTypeResourceList) throws IOException {
        if (!CollectionUtils.isEmpty(productTypeResourceList)) {
            for (ProductTypeResource productTypeResource : productTypeResourceList) {
                addProductTypeLinks(productTypeResource);
            }
        }
    }

    private void addProductTypeLinks(ProductTypeResource productTypeResource) throws IOException {
        addSelfLink(productTypeResource);
    }

    private void addSelfLink(ProductTypeResource productTypeResource) throws IOException {
        productTypeResource.add(linkTo(methodOn(ProductTypeController.class)
                .getProductTypeById(productTypeResource.getProductTypeId())).withSelfRel());
    }
}
