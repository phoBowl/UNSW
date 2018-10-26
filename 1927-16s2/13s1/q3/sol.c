int *componentOf;
int order;
int nComponents(Graph g)
{ 
  int i;
  componentOf = malloc(g->nV*sizeof(int));
  for(i=0;i<g->nV;i++){
     componentOf[i] =-1;
  }
 
  int comp =0;
  order =0;
  while(order < g->nV){
  	 Vertex v;
     for(v=0;v<g->nV;v++){
     	if(componentOf[v] == -1) break;
     }
     dfsComponents(g,v,comp);
     
     comp++;
  }
  return comp;
}

void dfsComponents(Graph g, Vertex v , int c){
	componentOf[v] =c;
	order ++;
	Vertex w;
	for(w =0;w< g->nV;w++){
		if(g->edges[v][w] &&  componentOf[w] == -1){
			dfsComponents(g,w,c);
		}
	}
}
