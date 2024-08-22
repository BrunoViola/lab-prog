package com.example.Prova1AdminBrunoViola;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ItemCardapioController {
    @Autowired
    ItemCardapioRepository itemCardapioRepository;
    @Autowired
    RestauranteRepository restauranteRepository;

    @GetMapping("/novo-itemCardapio/{idRestaurante}")
    public String mostrarFormNovoItemCardapio(@PathVariable int idRestaurante, Model model) {
        model.addAttribute("itemCardapio", new ItemCardapio());
        model.addAttribute("idRestaurante", idRestaurante);
        return "novo-itemCardapio";
    }

    @GetMapping(value = {"/cardapio-restaurante"})
    public String mostrarListaItemCardapio(Model model) {
        model.addAttribute("itemsCardapio", itemCardapioRepository.findAll());
        return "cardapio-restaurante";
    }

    @PostMapping("/adicionar-itemCardapio/{idRestaurante}")
    public String adicionarItemCardapio(@Valid ItemCardapio itemCardapio, BindingResult result,
                                        @PathVariable("idRestaurante") int idRestaurante) {
        if (result.hasErrors()) {
            return "/novo-itemCardapio";
        }
        Restaurante restaurante =
        restauranteRepository.findById(idRestaurante).orElseThrow(() -> new
        IllegalArgumentException("Restaurante inválido"));
        itemCardapio.setRestaurante(restaurante);
        itemCardapioRepository.save(itemCardapio);
        return "redirect:/cardapio-restaurante/" + idRestaurante;
    }

    @GetMapping("/editar-itemCardapio/{id}")
    public String mostrarFormAtualizarItem(@PathVariable("id") int id, Model model) {
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O id do Item do Cardápio é inválido:" + id));
        model.addAttribute("itemCardapio", itemCardapio);
        return "atualizar-itemCardapio";
    }

    @PostMapping("/editar-itemCardapio/{id}")
    public String atualizarItemCardapio(@PathVariable("id") int id, @Valid ItemCardapio
            itemCardapio, BindingResult result, Model model) {
        if (result.hasErrors()) {
            itemCardapio.setId(id);
            return "atualizar-itemCardapio";
        }
        itemCardapioRepository.save(itemCardapio);
        int idRestaurante = itemCardapio.getRestaurante().getId();
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante inválido"));

        //aqui eu defino o restaurante associado ao itemCardapio em questão
        itemCardapio.setRestaurante(restaurante);
        return "redirect:/cardapio-restaurante/"+idRestaurante;
    }

    @GetMapping("/remover-itemCardapio/{id}")
    public String removerItemCardapio(@PathVariable("id") int id, HttpServletRequest
            request) {
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O ID do Item do Cardápio é " +
                        "inválido:" + id));
        int idRestaurante = itemCardapio.getRestaurante().getId();


        itemCardapioRepository.delete(itemCardapio);


        return "redirect:/cardapio-restaurante/"+idRestaurante;
    }
}