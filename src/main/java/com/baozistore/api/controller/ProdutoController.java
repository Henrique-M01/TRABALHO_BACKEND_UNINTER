package com.baozistore.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.ProdutoRepository;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@PostMapping
	public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
		if (produto.getEstoque() == null) {
			produto.setEstoque(true);
		}
		Produto salvo = produtoRepository.save(produto);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	@GetMapping
	public List<Produto> listar() {
		return produtoRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		if (produto.isPresent()) {
			return ResponseEntity.ok(produto.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto dados) {
		Optional<Produto> produtoExistente = produtoRepository.findById(id);
		if (produtoExistente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Produto produto = produtoExistente.get();
		produto.setNome(dados.getNome());
		produto.setPreco(dados.getPreco());
		if (dados.getEstoque() != null) {
			produto.setEstoque(dados.getEstoque());
		}
		return ResponseEntity.ok(produtoRepository.save(produto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!produtoRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		produtoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
