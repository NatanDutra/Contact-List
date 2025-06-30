package com.projects.contact_list.controller;

import com.projects.contact_list.dto.ContactRecordDto;
import com.projects.contact_list.model.ContactModel;
import com.projects.contact_list.repositories.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ContactController {

    @Autowired
    ContactRepository ContactRepository;

    @PostMapping("/products")
    public ResponseEntity<ContactModel> saveProduct(@RequestBody @Valid ContactRecordDto ContactRecordDto){
        var ContactModel = new ContactModel();
        //Conversão do que chega em DTO informado pelo usuário para o model para salvar no banco
        BeanUtils.copyProperties(ContactRecordDto, ContactModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContactRepository.save(ContactModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ContactModel>> getAllProducts() {
        List<ContactModel> productsList = ContactRepository.findAll();
        if(!productsList.isEmpty()){
            for(ContactModel product : productsList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ContactController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
        Optional<ContactModel> productO = ContactRepository.findById(id);
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        productO.get().add(linkTo(methodOn(ContactController.class).getAllProducts()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(productO.get());
    }

    @PutMapping("products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ContactRecordDto ContactRecordDto){
        Optional<ContactModel> productO = ContactRepository.findById(id);
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        var ContactModel = productO.get();
        BeanUtils.copyProperties(ContactRecordDto, ContactModel);
        return ResponseEntity.status(HttpStatus.OK).body(ContactRepository.save(ContactModel));
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id){
        Optional<ContactModel> productO = ContactRepository.findById(id);
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        ContactRepository.delete(productO.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }
}
