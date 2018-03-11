import java.util.*;

class AllMinSpanTree{
	static class Edge{
		int u,v,w;
		public Edge(int u, int v, int w){
			this.u=u;
			this.v=v;
			this.w=w;
		}
	}
	static class Graph{
		int V,E;

		ArrayList<Edge> edges;
		ArrayList<Edge> original_edges;
		ArrayList<Edge> MST;

		int MST_weight;

		public Graph(int V, int E){
			this.V = V;
			this.E = E;
			edges = new ArrayList<Edge>();
			original_edges = new ArrayList<Edge>();
			MST = new ArrayList<Edge>();
		}

		public Graph(Graph g){
			V = g.V;
			E = g.E;
			edges = new ArrayList<Edge>();
			original_edges = new ArrayList<Edge>();
			for(Edge edge : g.edges){
				edges.add(new Edge(edge.u, edge.v, edge.w));
			}
			for(Edge edge : g.original_edges){
				original_edges.add(new Edge(edge.u, edge.v, edge.w));
			}
			MST = new ArrayList<Edge>();

		}

		public void addEdge(Edge e){
			edges.add(e);
			original_edges.add(e);
		}

		public void removeEdge(Edge e){
			edges.remove(e);
		}

		public void modifyWeight(Edge e, int w){
			for(Edge edge:edges){
				if(edge.u==e.u && edge.v==e.v || edge.u==e.v && edge.v==e.u){
					edge.w=w;
					break;
				}
			}
		}

		public boolean containsCycle(boolean[] visitedArray){
			for(Edge edge : edges){
				int u = edge.u;
				int v = edge.v;
				if(visitedArray[u] && visitedArray[v])
					return true;
				visitedArray[u] = true;
				visitedArray[v] = true;
			}
			return false;
		}

		public boolean isTree(){
			boolean[] visitedArray = new boolean[this.V];
			for(int i=0; i<this.V; i++){
				visitedArray[i]=false;
			}

			boolean cycle = containsCycle(visitedArray);

			if(cycle)
				return false;
			for(int i=0; i<V; i++){
				// cout << i << " is visited " << visitedArray[i] << endl;;
				if(!visitedArray[i])
					return false;
			}
			return true;
		}

		public boolean isConnected(){
			boolean[] visitedArray = new boolean[this.V];
			for(int i=0; i<this.V; i++){
				visitedArray[i]=false;
			}

			for(Edge edge : edges){
				int u = edge.u;
				int v = edge.v;
				visitedArray[u] = true;
				visitedArray[v] = true;
			}

			for(int i=0; i<this.V; i++){
				// cout << i << " is visited " << visitedArray[i] << endl;;
				if(!visitedArray[i])
					return false;
			}
			return true;
		}

		static class DisjointSet{
			int[] parent;
			int[] rnk;
			int n;
			public DisjointSet(int n){
		        this.n = n;
		        parent = new int[n+1];
		        rnk = new int[n+1];

		        for(int i=0;i<=n;i++){
		            rnk[i] = 0;
		            parent[i] = i;
		        }
		    }
		    public int Find(int u){
		        if(u == parent[u]) return parent[u];
		        else return Find(parent[u]);
		    }
		    void Union(int x,int y){
		        x = Find(x);
		        y = Find(y);
		        if(x != y){
		            if(rnk[x] < rnk[y]){
		                rnk[y] += rnk[x];
		                parent[x] = y;
		            }
		            else{
		                rnk[x] += rnk[y];
		                parent[y] = x;
		            }
		        }
		    }
		}

		public int kruskalMST(){
		    int MSTWeight = 0; //sum of all vertex weights
		    MST_weight = 0;
		    Collections.sort(edges,new Comparator<Edge>() {
			    @Override
			    public int compare(Edge o1, Edge o2) {
			        return o1.w - o2.w;
			    }
			});

		    //for all u in G_v
		    //    MAKE-SET(u)
		    DisjointSet ds = new DisjointSet(this.V);

		    
		    for(Edge it : edges){
		        int u = it.u;
		        int v = it.v;

		        int setU = ds.Find(u);
		        int setV = ds.Find(v);


		        if(setU != setV){
		            int w = it.w;
		            MST.add(new Edge(u,v,w));
		            MSTWeight += w;
		            ds.Union(setU,setV);
		        }
		    }

		    // get real weight
		    for(Edge it : MST){
		        int u = it.u;
		        int v = it.v;
		        
		        for(Edge it2 : original_edges){
		            int u2 = it2.u;
		            int v2 = it2.v;
		            if(u==u2 && v==v2){
		                MST_weight+=it2.w;
		                break;
		            }
		        }
		    }
		    // MST_weight = MSTWeight;
		    return MSTWeight;
		}
	}


	public static void printMST(Graph g){	    
	    for(Edge it : g.MST){
	        System.out.println(it.u + " - " + it.v + " wt " + it.w);
	    }
	}



	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		int V = scn.nextInt();
		int E = scn.nextInt();

		Graph g = new Graph(V,E);

		for(int i=0;i<E;i++){
	        int u = scn.nextInt();
	        int v = scn.nextInt();
	        int w = scn.nextInt();
	        Edge e = new Edge(u,v,w);
	        g.addEdge(e);
	    }

	    PriorityQueue<Graph> partitions = new PriorityQueue<Graph>(10,new Comparator<Graph>() {
			    @Override
			    public int compare(Graph o1, Graph o2) {
			        return (int)o1.MST_weight - (int)o2.MST_weight;
			    }
			});

	    if(g.isConnected()){
	    	double weight = g.kruskalMST();	  
	   		partitions.add(g);

	   		while(!partitions.isEmpty()){
	   			Graph smallest = partitions.poll();

	   			weight = smallest.MST_weight;
	   			System.out.println("MST: ");
	   			printMST(smallest);
	   			System.out.println("Weight of MST is: " + weight);
	   		

		   		int i = 0;
	    		for(Edge edge : smallest.MST){
	    			if(edge.w==0)
	    				i++;
	    			else
	    				break;
	    		}


	    		for(int j=0; j<smallest.V-1-i; j++){
		    		// create deep copy
		    		Graph part = new Graph(smallest);
	               
		    		// add/remove edges accordingly

		    		// the edges that we need to include -> make their weight 0 -> equivalent
	    		
	                for(int z=i; z<i+j; z++){
	                    for(Edge edge2 : part.edges){
	                        if(smallest.MST.get(z).u == edge2.u && smallest.MST.get(z).v == edge2.v){
		    			        part.modifyWeight(edge2, 0);
	                            break;
	                        }
	                    }
		    		}

		    		// the edges that we need to exclude -> remove them from graph
		    		
		    		for(int z=0; z<part.edges.size(); z++){
		    			if(smallest.MST.get(i+j).u == part.edges.get(z).u && smallest.MST.get(i+j).v == part.edges.get(z).v){
		    				part.edges.remove(z);
		    				break;
		    			}
		    		}

		    		if(part.isConnected()){
		    			part.kruskalMST();
		    			if(part.MST.size()<part.V-1)
		    				continue;
		    			partitions.add(part);
		    		}
		    		else
		    			continue;
		    	}
		    }

	    }
	    else{
	    	System.out.println("Graph not isConnected");
	    }
	}
}