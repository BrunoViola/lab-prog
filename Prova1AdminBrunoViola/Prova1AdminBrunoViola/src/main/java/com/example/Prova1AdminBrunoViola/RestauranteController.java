package com.example.Prova1AdminBrunoViola;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RestauranteController {
    @Autowired
    RestauranteRepository restauranteRepository;
    @Autowired
    ItemCardapioRepository itemCardapioRepository;

    @GetMapping("/novo-restaurante")
    public String mostrarFormNovoRestaurante(Restaurante restaurante){
        return "novo-restaurante";
    }
    @GetMapping(value={"/index", "/"})
    public String mostrarListaRestaurante(Model model) {
        model.addAttribute("restaurantes", restauranteRepository.findAll());
        return "index";
    }
    @PostMapping("/adicionar-restaurante")
    public String adicionarRestaurante(@Valid Restaurante restaurante, BindingResult result) {
        if (result.hasErrors()) {
            return "/novo-restaurante";
        }
        restauranteRepository.save(restaurante);
        return "redirect:/index";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormAtualizar(@PathVariable("id")int id, Model model) {
        Restaurante restaurante=restauranteRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException(
                        "O id do Restaurante é inválido:"+id));
        model.addAttribute("restaurante",restaurante);
        return "atualizar-restaurante";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarRestaurante(@PathVariable("id")int id,@Valid Restaurante
                                 restaurante, BindingResult result,Model model){
        if(result.hasErrors()){
            restaurante.setId(id);
            return"atualizar-restaurante";
        }
        restauranteRepository.save(restaurante);
        return"redirect:/index";
    }

    @GetMapping("/remover/{id}")
    public String removerRestaurante(@PathVariable("id")int id){
        Restaurante restaurante=restauranteRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("O id do Restaurante é inválido:"+id));
        restauranteRepository.delete(restaurante);
        return"redirect:/index";
    }

    @GetMapping("/cardapio-restaurante/{idRestaurante}")
    public String mostrarCardapioRestaurante(@PathVariable int idRestaurante, Model model) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElseThrow(() -> new IllegalArgumentException("Restaurante inválido"));
        model.addAttribute("restaurante", restaurante);
        model.addAttribute("itemsCardapio",
                itemCardapioRepository.findByRestauranteId(idRestaurante));
        return "cardapio-restaurante";
    }
}