package com.example.starter.web;

import com.example.starter.domain.Product;
import com.example.starter.dto.ProductForm;
import com.example.starter.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String keyword, Model model) {
        model.addAttribute("products", productService.search(keyword));
        model.addAttribute("q", keyword);
        model.addAttribute("pageTitle", "Products");
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("pageTitle", "New Product");
        model.addAttribute("formAction", "/products");
        return "products/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "New Product");
            model.addAttribute("formAction", "/products");
            return "products/form";
        }
        productService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Product created.");
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setQuantity(product.getQuantity());
        model.addAttribute("productForm", form);
        model.addAttribute("pageTitle", "Edit Product");
        model.addAttribute("formAction", "/products/" + id);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Product");
            model.addAttribute("formAction", "/products/" + id);
            return "products/form";
        }
        productService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Product updated.");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted.");
        return "redirect:/products";
    }
}
