package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import calculadores.CalculadorGenerico;
import calculadores.CirculoCalculador;
import calculadores.PoligonoCalculador;
import calculadores.RetaCalculador;
import calculadores.RetanguloCalculador;
import controladores.TipoDesenho;
import controladores.TipoPrimitivo;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import primitivos.Circulo;
import primitivos.Poligono;
import primitivos.Ponto;
import primitivos.PontoGr;
import primitivos.Reta;
import primitivos.Retangulo;


@SuppressWarnings("restriction")
public class Desenhador {

	private Color cor;
	private int diametro;
	private Canvas canvas;
	private Canvas canvasLittle;
	private Poligono poligonoEmDesenho;
	private Map<TipoPrimitivo, List<Object>> objetosDesenhados;  
	private Map<TipoPrimitivo, List<Integer>> indicesSelecionados;
	private Rectangle2D areaRecorte;
	
	public Desenhador(Canvas canvas, Canvas canvasLittle) {
		this.diametro = 2;
		this.cor = Color.BLACK;
		this.canvas = canvas;
		this.canvasLittle = canvasLittle;
		this.inicilizarEstruturasManipulacaoDeDesenhos();
	}
	
	public Map<TipoPrimitivo, List<Object>> getObjetosDesenhados() {
		return objetosDesenhados;
	}

	public void setObjetosDesenhados(Map<TipoPrimitivo, List<Object>> objetosDesenhados) {
		this.objetosDesenhados = objetosDesenhados;
	}

	public Poligono getPoligonoEmDesenho() {
		return poligonoEmDesenho;
	}

	public void setPoligonoEmDesenho(Poligono poligonoEmDesenho) {
		this.poligonoEmDesenho = poligonoEmDesenho;
	}

	public Rectangle2D getAreaRecorte() {
		return areaRecorte;
	}

	public void setAreaRecorte(Rectangle2D areaRecorte) {
		this.areaRecorte = areaRecorte;
	}
	
	public Color getCor() {
		return cor;
	}


	public void setCor(Color cor) {
		this.cor = cor;
	}

	public void setDiametro(int diametro) {
		this.diametro = diametro;
	}


	public void inicilizarEstruturasManipulacaoDeDesenhos(){
		objetosDesenhados = new HashMap<>();
		indicesSelecionados = new HashMap<>();
		List<TipoPrimitivo> listEnum = Arrays.asList(TipoPrimitivo.values());
		
		for ( TipoPrimitivo tipoPrimitivo: listEnum) {
			objetosDesenhados.put(tipoPrimitivo, new ArrayList<>());
			indicesSelecionados.put(tipoPrimitivo, new ArrayList<>());
		}
	}
	
	public void desenharPrimitivoElastico(Ponto pontoInicial, Ponto pontoFinal, TipoDesenho tipoDesenho, boolean salvar) {
		switch(tipoDesenho) {
			case RETA_ELASTICA:
				desenharReta(pontoInicial,pontoFinal, salvar);
				break;
			case CIRCULO_ELASTICO:
				desenharCirculo(pontoInicial,pontoFinal, salvar);
				break;
			case POLIGONO_ELASTICO:
			case RETA_POLIGONAL:
				desenharPoligono(pontoInicial,pontoFinal, salvar);
				break;
			case RETANGULO_ELASTICO:
				desenharRetangulo(pontoInicial,pontoFinal, salvar);
				break;
			case SELECIONAR_AREA_CLIPPING:
				desenharAreaSelecao(pontoInicial, pontoFinal, salvar);
		}
	}
	
	public void desenharReta(Ponto pontoInicial, Ponto pontoFinal, boolean salvar) {
		Reta reta = new Reta(pontoInicial, pontoFinal, cor);
		desenharPontos(RetaCalculador.obterPontosAlgoritmoMidPoint(reta), cor);
		if (salvar) salvarPrimitivoDesenhado(TipoPrimitivo.RETA,reta);
	}
	
	public void desenharCirculo(Ponto pontoInicial, Ponto pontoFinal, boolean salvar) {
		Ponto pontoMedio = CalculadorGenerico.obterPontoMedio(pontoInicial, pontoFinal);
		int raio = CirculoCalculador.obterRaio(pontoMedio, pontoFinal);
		Circulo circulo = new Circulo(raio, pontoMedio, cor);
		desenharPontos(CirculoCalculador.obterPontosAlgoritmoMidPoint(circulo), cor);
		if (salvar) salvarPrimitivoDesenhado(TipoPrimitivo.CIRCULO, circulo);
	}
	
	private void desenharRetangulo(Ponto pontoInicial, Ponto pontoFinal, boolean salvar) {
		Retangulo retangulo = new Retangulo(pontoInicial, pontoFinal, cor);
		desenharPontos(RetanguloCalculador.obterPontos(retangulo), cor);
		if (salvar) salvarPrimitivoDesenhado(TipoPrimitivo.RETANGULO, retangulo);
	}
	
	private void desenharAreaSelecao(Ponto pontoInicial, Ponto pontoFinal, boolean salvarAreaRecorte) {
		Retangulo retangulo = new Retangulo(pontoInicial, pontoFinal, cor);
		List<Ponto> pontos = RetanguloCalculador.obterPontos(retangulo);
		List<Ponto> pontosFiltrados = pontos.stream().filter(pt -> pontos.indexOf(pt)%10 == 0).collect(Collectors.toList());
		desenharPontos(pontosFiltrados, cor);
		if (salvarAreaRecorte) {
			double largura = Math.floor(retangulo.getDiagonalMax().getx() - retangulo.getDiagonalMin().getx() / 5);
			double altura = Math.floor(retangulo.getDiagonalMax().gety() - retangulo.getDiagonalMin().gety() / 5);

			double minX = Math.floor((retangulo.getDiagonalMin().getx() + 60) / 5);
			double minY = Math.floor(retangulo.getDiagonalMin().gety() / 5);

			this.setAreaRecorte(new Rectangle2D(minX, minY, largura, altura));
		}
	}
	
	public void desenharPoligono(Ponto pontoInicial, Ponto pontoFinal, boolean addReta) {
		Reta reta = new Reta(pontoInicial, pontoFinal, cor);
		if (addReta) { 
			poligonoEmDesenho.addReta(reta);
		}else {
			desenharReta(reta.getA(), reta.getB(), false);
		}
		desenharPontos(PoligonoCalculador.obterPontos(poligonoEmDesenho), cor);
	}

	public void desenharPontos(List<Ponto> pontos, Color cor) {
		for (Ponto p : pontos) {
			desenharPonto((int) Math.floor(p.getx()), (int) Math.floor(p.gety()), "", cor);
		}
	}
	
	public void desenharPonto(int x, int y) {
		this.desenharPonto(x, y, "", cor);
	}

	public void desenharPonto(int x, int y, String nome, Color cor) {
		PontoGr p;
		p = new PontoGr(x, y, cor, nome, diametro);
		p.desenharPonto(canvas.getGraphicsContext2D(), canvasLittle.getGraphicsContext2D());
	}
	
	private void salvarPrimitivoDesenhado(TipoPrimitivo tipoPrimitivo,Object primitivo){
		objetosDesenhados.get(tipoPrimitivo).add(primitivo);
	}
	
	public void salvarPoligonoDesenhado(TipoPrimitivo tipoPrimitivo) {
		objetosDesenhados.get(tipoPrimitivo).add(this.poligonoEmDesenho);
	}
	
	public void desenharObjetosArmazenados(Color novaCor){
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasLittle.getGraphicsContext2D().clearRect(0, 0, canvasLittle.getWidth(), canvasLittle.getHeight());

		objetosDesenhados.forEach((tipoPrimitivo, objetos) -> {
			for(Object desenho : objetos){
				boolean selecionado = (this.indicesSelecionados.get(tipoPrimitivo).contains(objetos.indexOf(desenho)));
				Color cor;
				switch (tipoPrimitivo) {
					case RETA:
						Reta reta = (Reta) desenho;
						cor = (selecionado) ? novaCor : reta.getCor() ;
						this.desenharPontos(RetaCalculador.obterPontosAlgoritmoMidPoint(reta), cor);
						break;
					case RETANGULO:
						Retangulo retangulo = (Retangulo) desenho;
						cor = (selecionado) ? novaCor : retangulo.getCor() ;
						this.desenharPontos(RetanguloCalculador.obterPontos(retangulo), cor);
						break;
					case POLIGONO:
					case LINHA_POLIGONAL:
						Poligono poligono = (Poligono) desenho;
						cor = (selecionado) ? novaCor : poligono.getCor() ;
						this.desenharPontos(PoligonoCalculador.obterPontos(poligono), cor);
						break;
					case CIRCULO:
						Circulo circulo = (Circulo) desenho;
						cor = (selecionado) ? novaCor : circulo.getCor() ;
						desenharPontos(CirculoCalculador.obterPontosAlgoritmoMidPoint(circulo), cor );
						break;
				}
			}
		});
	}
	
	public boolean isPoligonoElasticoEmDesenho() {
		return (poligonoEmDesenho != null && poligonoEmDesenho.getRetas().size() >= 2);
	}

}