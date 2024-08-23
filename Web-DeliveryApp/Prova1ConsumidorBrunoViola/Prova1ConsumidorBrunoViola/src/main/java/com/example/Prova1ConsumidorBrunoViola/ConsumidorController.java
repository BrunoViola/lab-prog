package com.example.Prova1ConsumidorBrunoViola;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ConsumidorController {
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ItemCardapioRepository itemCardapioRepository;
    private static final String SESSION_PEDIDOS=
            "sessionPedidos";
    @GetMapping(value={"/index", "/"})
    public String mostrarPaginaInicial(Model model) {
        return "index";
    }

    @GetMapping(value={"/restaurantes"})
    public String mostrarListaRestaurante(Model model) {
        model.addAttribute("restaurantes", restauranteRepository.findAll()); //atribui todos os
        // restaurante a "restaurantes"
        return "restaurantes";
    }
    @GetMapping("/cardapio/{idRestaurante}")
    public String mostrarCardapioRestaurante(@PathVariable int idRestaurante, Model model) { //a
        // função recebe o id do restaurante
        Restaurante restaurante =
                restauranteRepository.findById(idRestaurante).orElseThrow(() -> new IllegalArgumentException("Restaurante inválido")); //ela encontra qual é o restaurante
        model.addAttribute("restaurante", restaurante); //atribui o restaurante a "restaurante"
        model.addAttribute("itemsCardapio",
                itemCardapioRepository.findByRestauranteId(idRestaurante)); //encontra todos os
        // itens daquele restaurante pelo id e atribui a "itemsCardapio"
        return "cardapio";
    }


    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model, HttpServletRequest request) {
        List<ItemCardapio> itemsCardapio = (List<ItemCardapio>) request.getSession().getAttribute(SESSION_PEDIDOS);
        Double valorAtual = (Double) request.getSession().getAttribute("valorTotal");
        valorAtual = 0.0; //valor total dos itens é iniciado com zero
        if (itemsCardapio != null) {
            List<ItemCardapio> updatedItems = new ArrayList<>();
            for (ItemCardapio pedido : itemsCardapio) {
                Integer id = pedido.getId(); //pego o id do pedido que está armazenado no banco
                // de dados, se o id não existir é pq o itemCardapio não existe mais

                // Verifica se o ID do pedido é válido
                if (id != null) {
                    Optional<ItemCardapio> optionalPedido = itemCardapioRepository.findById(id);
                    if (optionalPedido.isPresent()) {
                        //aqui eu atualizo todos os pedidos presentes na sessão com as
                        // informações do banco de dados
                        ItemCardapio itemCardapio = optionalPedido.get();
                        pedido.setNome(itemCardapio.getNome());
                        pedido.setPreco(itemCardapio.getPreco());
                        pedido.setDescricao(itemCardapio.getDescricao());
                        pedido.setRestaurante(itemCardapio.getRestaurante());
                        valorAtual = valorAtual+pedido.getPreco()* pedido.getQuantidade();
                        request.getSession().setAttribute("valorTotal", valorAtual);
                        updatedItems.add(pedido);//quando um pedido não existe mais no banco de
                        // dados, ele não será adicionado à lista updatedItems
                    }
                }
            }

            request.getSession().setAttribute(SESSION_PEDIDOS, updatedItems);//atualiza a lista
            // de itens do cardápio na sessão com os itens atualizados e existentes
        }

        model.addAttribute("sessionPedidos", !CollectionUtils.isEmpty(itemsCardapio) ? itemsCardapio : new ArrayList<>());
        model.addAttribute("valorTotal", valorAtual);

        return "pedidos"; //mostro o pedidos.html
    }


    @GetMapping("/pedir/{id}")
    public String fazerPedido(@PathVariable("id")int id,
                              @RequestParam("idRestaurante") int idRestaurante,
                                 HttpServletRequest request){
        ItemCardapio itemCardapio=itemCardapioRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("O id do item é inválido:"+id));
        List<ItemCardapio>pedidos=(List<ItemCardapio>)
                request.getSession().getAttribute(SESSION_PEDIDOS); //atribuo a pedidos todos os
        // itens presentes na sessão
        if(CollectionUtils.isEmpty(pedidos)){//se a sessão estiver vazia
            pedidos=new ArrayList<>();//crio um arraylist
        }
        if (pedidos.contains(itemCardapio)) {//se nesse pedido consta o item
            int index = pedidos.indexOf(itemCardapio); //encontro o index dele
            ItemCardapio itemExistente = pedidos.get(index); //encontro o item e atribuo a
            // "itemExistente"
            itemExistente.setQuantidade(itemExistente.getQuantidade()+1);//aumento em um a
            // quantidade do item
        }else{
            itemCardapio.setQuantidade(1);//se nao, eu 'seto' a quantidade dele como 1
            pedidos.add(itemCardapio);//adiciono o item que nao existia ao ArrayList
        }
        request.getSession().setAttribute(SESSION_PEDIDOS,pedidos);//pego a sessao e 'seto'
        // pedidos para atualizar a lista
        return "redirect:/pedidos";
    }

    @GetMapping("/pedidos/remover/{id}")
    public String removerPedido(@PathVariable("id") int id, HttpServletRequest
            request) {
        List<ItemCardapio> sessionPedidos =
                (List<ItemCardapio>) request.getSession().getAttribute(SESSION_PEDIDOS);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException(
                        "O id do item é inválido: " + id));
        sessionPedidos.remove(itemCardapio);
        request.getSession().setAttribute(SESSION_PEDIDOS, sessionPedidos);

        return "redirect:/pedidos";
    }

    @GetMapping("/pedidos/aumentar/{id}")
    public String aumentarPedido(@PathVariable("id") int id,
                                 Model model, HttpServletRequest request){
        List<ItemCardapio> sessionPedidos =
                (List<ItemCardapio>) request.getSession().getAttribute(SESSION_PEDIDOS);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("O id do item é inválido: " + id));
        if (sessionPedidos.contains(itemCardapio)) {
            int index = sessionPedidos.indexOf(itemCardapio);
            ItemCardapio itemExistente = sessionPedidos.get(index);

            if (itemExistente != null) {
                itemExistente.setQuantidade(itemExistente.getQuantidade() + 1);
            }
        }

        request.getSession().setAttribute(SESSION_PEDIDOS, sessionPedidos);
        return "redirect:/pedidos";
    }
    @GetMapping("/pedidos/diminuir/{id}")
    public String diminuirPedido(@PathVariable("id") int id, HttpServletRequest request){
        List<ItemCardapio> sessionPedidos =
                (List<ItemCardapio>) request.getSession().getAttribute(SESSION_PEDIDOS);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("O id do item é inválido: " + id));
        if (sessionPedidos.contains(itemCardapio)) {
            int index = sessionPedidos.indexOf(itemCardapio);
            ItemCardapio itemExistente = sessionPedidos.get(index);

            if ((itemExistente != null)) {
                int piso = itemExistente.getQuantidade();
                if(piso>=1)
                    itemExistente.setQuantidade(itemExistente.getQuantidade() - 1);

                if(itemExistente.getQuantidade()==0){
                    sessionPedidos.remove(itemCardapio);
                }
            }
        }
        request.getSession().setAttribute(SESSION_PEDIDOS, sessionPedidos);
        return "redirect:/pedidos";
    }



}
