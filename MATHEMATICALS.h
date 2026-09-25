#include<stdio.h>

#include<stdlib.h>
const long long N_base=10LL;
const long long N_maxdgt=N_base-1LL;
//==============================================================================
    static struct NODEi64{
        struct NODEi64* l;
        struct NODEi64* r;
        long long v;
    };
    static const size_t sizeof$NODEi64$=8+sizeof(void*)*2;

    static void free$NODEi64$(struct NODEi64* n);
    //==========================================================================
    static void free$NODEi64$(struct NODEi64* n){
        struct NODEi64* r;
        while(n){
            r=n->r;
            free(n);
            n=r;
        }
    }
//==============================================================================
    struct N{
        struct NODEi64  n0;
        struct NODEi64* np;
    };
    const size_t sizeof$N$=8+sizeof(void*)*3;

    struct N* N(long long v);

    void free$N$(struct N* o);

    struct N*     N_clone(struct N* v);
    unsigned char N_equiv(struct N* x,struct N* y);
    short         N_order(struct N* x,struct N* y);
    void          N_setto(struct N* o,struct N* v);

    struct N*     N_add        (struct N* x,struct N* y);
    void          N_addby      (struct N* x,struct N* y);
    void          N_addby1     (struct N* x);            //may have better solutions ?
    void          N_addbybase  (struct N* x);            //may have better solutions ?

    struct N*     N_minus      (struct N* x,struct N* y);
    void          N_minusby    (struct N* x,struct N* y);
    void          N_minusby1   (struct N* x);            //may have better solutions ?
    void          N_minusbybase(struct N* x);            //may have better solutions ?

    void          N_multbybase (struct N* x);
    struct N*     N_multi64    (struct N* x,long long y);
    struct N*     N_mult       (struct N* x,struct N* y);//may have better solutions ?
    void          N_multby     (struct N* x,struct N* y);//

    unsigned char N_divbybase  (struct N* m);            //
    unsigned char N_half       (struct N* m);
    unsigned char N_divby      (struct N* m,struct N* n);//
    struct N*     N_div        (struct N* m,struct N* n);//
    unsigned char N_divisibility(struct N* q);

    struct N* N_pow(struct N* b,struct N* p);   //
    struct N* N_factorial(struct N* n);         //
    //==========================================================================
    struct N* N(long long v){
        struct N* o=malloc(sizeof$N$);
        long long q=v/N_base;
        (o->np=&o->n0)->l=NULL;
                o->n0 . v=v-N_base*q;
        while(v=q)
            (o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=v-N_base*(q=v/N_base);
        o->np->r=NULL;
        return o;
    }

    void free$N$(struct N* o){
        free$NODEi64$(&o->n0);
    }

    struct N*     N_clone(struct N* v){
        struct N* o=malloc(sizeof$N$);
        struct NODEi64* n=&v->n0;
        (o->np=&o->n0)->l=NULL;
                o->n0 . v=n->v;
        while(n=n->r)
            (o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=n->v;
        o->np->r=NULL;
        return o;
    }
    unsigned char N_equiv(struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        do  if(nx->v!=ny->v)
                return 0;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        return nx==ny;
    }
    short         N_order(struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long d=0LL;
        do  if(nx->v!=ny->v)
                d=nx->v-ny->v;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        if(nx)return 1;
        if(ny)return-1;
        if(d<0LL)return-1;
        if(d>0LL)return 1;
        return 0;
    }
    void          N_setto(struct N* o,struct N* v){
        struct NODEi64* no=&o->n0;
        struct NODEi64* nv=&v->n0;
        struct NODEi64* end;
        do  (end=no)->v=nv->v;
        while(
            (no=no->r)!=NULL&
            (nv=nv->r)!=NULL
        );
        while(nv){
            (o->np=((o->np->r=malloc(sizeof$NODEi64$))->l=o->np)->r)->v=nv->v;
            nv=nv->r;
        }
        o->np->r=NULL;free$NODEi64$(end->r);//deletable
        (o->np=end)->r=NULL;
    }

    struct N*     N_add        (struct N* x,struct N* y){
        struct N* z=malloc(sizeof$N$);
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt=nx->v+ny->v;
        long long q=dgt/N_base;
        (z->np=&z->n0)->l=NULL;
                z->n0 . v=dgt-N_base*q;
        while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        ){  q=(dgt=q+
                nx->v+
                ny->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
        }
        while(nx){
            q=(dgt=q+
                nx->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            nx=nx->r;
        }
        while(ny){
            q=(dgt=q+
                ny->v
            )/N_base;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            ny=ny->r;
        }
        if(q)
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=1LL;
        z->np->r=NULL;
        return z;
    }
    void          N_addby      (struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt;
        long long q=0LL;
        do{ q=(dgt=q+
                nx->v+
                ny->v
            )/N_base;
            nx->v=dgt-N_base*q;
        }while(
            (nx=nx->r)!=NULL&
            (ny=ny->r)!=NULL
        );
        while(nx){
            q=(dgt=q+
                nx->v
            )/N_base;
            nx->v=dgt-N_base*q;
            nx=nx->r;
        }
        while(ny){
            q=(dgt=q+
                ny->v
            )/N_base;
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=dgt-N_base*q;
            ny=ny->r;
        }
        if(q)
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
        x->np->r=NULL;
    }
    void          N_addby1     (struct N* x){
        struct NODEi64* n=&x->n0;
        long long q=1LL;
        do  if(n->v==N_maxdgt){
                n->v=0LL;
                n=n->r;
            }else{
                ++n->v;
                q=0LL;
            }
        while(q!=0LL&n!=NULL);
        if(q){
            (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
             x->np->r=NULL;
        }
    }
    void          N_addbybase  (struct N* x){
        struct NODEi64* n=x->n0.r;
        if(n){
            long long q=1LL;
            do  if(n->v==N_maxdgt){
                    n->v=0LL;
                    n=n->r;
                }else{
                    ++n->v;
                    q=0LL;
                }
            while(q!=0LL&n!=NULL);
            if(q){
                (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
                 x->np->r=NULL;
            }
        }else{  (x->np=((x->np->r=malloc(sizeof$NODEi64$))->l=x->np)->r)->v=1LL;
                 x->np->r=NULL;
        }
    }

    struct N*     N_minus      (struct N* x,struct N* y){
        struct N* z=malloc(sizeof$N$);
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt=nx->v-ny->v;
        long long q=(N_base+dgt)/N_base-1LL;
        struct NODEi64* end=z->np=&z->n0;
        end->l=NULL;
        end->v=dgt-N_base*q;
        nx=nx->r;
        while(ny=ny->r){
            q=(N_base+(dgt=q+
                nx->v-
                ny->v
            ))/N_base-1LL;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-=N_base*q;
            if(dgt)
                end=z->np;
            nx=nx->r;
        }
        while(nx){
            q=(N_base+(dgt=q+
                nx->v
            ))/N_base-1LL;
            (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-=N_base*q;
            if(dgt)
                end=z->np;
            nx=nx->r;
        }
        z->np->r=NULL;free$NODEi64$(end->r);//deletable
        (z->np=end)->r=NULL;
        return z;
    }
    void          N_minusby    (struct N* x,struct N* y){
        struct NODEi64* nx=&x->n0;
        struct NODEi64* ny=&y->n0;
        long long dgt;
        long long q=0LL;
        struct NODEi64* end=nx;
        do{ q=(N_base+(dgt=q+
                nx->v-
                ny->v
            ))/N_base-1LL;
            if((nx->v=dgt-=N_base*q))
                end=nx;
            nx=nx->r;
        }while(ny=ny->r);
        while(nx){
            q=(N_base+(dgt=q+
                nx->v
            ))/N_base-1LL;
            if((nx->v=dgt-=N_base*q))
                end=nx;
            nx=nx->r;
        }
        x->np->r=NULL;free$NODEi64$(end->r);//deletable
        (x->np=end)->r=NULL;
    }
    void          N_minusby1   (struct N* x){
        struct NODEi64* n=&x->n0;
        long long q=-1LL;
        do  if(n->v){
                --n->v;
                q=0LL;
            }else{
                n->v=N_maxdgt;
                n=n->r;
            }
        while(q!=0LL&n!=NULL);
        if(x->np->v==0LL&x->n0.r!=NULL){
            x->np=x->np->l;
            free(x->np->r);//deletable
            x->np->r=NULL;
        }
    }
    void          N_minusbybase(struct N* x){
        struct NODEi64* n=x->n0.r;
        long long q=-1LL;
        do  if(n->v){
                --n->v;
                q=0LL;
            }else{
                n->v=N_maxdgt;
                n=n->r;
            }
        while(q!=0LL&n!=NULL);
        if(x->np->v==0LL&x->n0.r!=NULL){
            x->np=x->np->l;
            free(x->np->r);
            x->np->r=NULL;
        }
    }

    void          N_multbybase (struct N* x){

        /*struct NODEi64* n0=&x->n0;
        if(n0->r!=NULL|n0->v!=0LL){
            struct NODEi64* n1=n0->r;
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            if(nn->r=n1)n1->l=nn;
            else        x->np=nn;
        }*/

        struct NODEi64* n0=&x->n0;
        struct NODEi64* n1=n0->r;
        if(n1){
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            (nn->r=n1)->l=nn;
        }else if(n0->v){
            struct NODEi64* nn=malloc(sizeof$NODEi64$);
            nn->v=((n0->r=nn)->l=n0)->v;
            n0->v=0LL;
            (x->np=nn)->r=n1;
        }

    }
    struct N*     N_multi64    (struct N* x,long long y){
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->l=NULL;
        if(y){
            struct NODEi64* n=&x->n0;
            long long dgt=n->v*y;
            long long q=dgt/N_base;
            z->n0.v=dgt-N_base*q;
            while(n=n->r){
                q=(dgt=q+n->v*y)/N_base;
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=dgt-N_base*q;
            }
            if(q)
                (z->np=((z->np->r=malloc(sizeof$NODEi64$))->l=z->np)->r)->v=q;
        }else
            z->n0.v=0LL;
        z->np->r=NULL;
        return z;
    }
    struct N*     N_mult       (struct N* x,struct N* y){
        struct N* z=malloc(sizeof$N$);
        (z->np=&z->n0)->v=0LL;
                z->n0 . l=
                z->n0 . r=NULL;
        struct NODEi64* n=&y->n0;
        struct N* xbp=N_clone(x);
        struct N* xbpy;
        do{ N_addby(z,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
            N_multbybase(xbp);
        }while(n=n->r);
        free$N$(xbp);//deletable
        return z;
    }
    void          N_multby     (struct N* x,struct N* y){
        struct N* xbp=N_clone(x);
        struct N* xbpy;
        free$NODEi64$(x->n0.r);//deletable
        (x->np=&x->n0)->r=NULL;
                x->n0 . v=0LL;
        struct NODEi64* n=&y->n0;
        do{ N_addby(x,xbpy=N_multi64(xbp,n->v));
            free$N$(xbpy);//deletable
            N_multbybase(xbp);
        }while(n=n->r);
        free$N$(xbp);//deletable
    }

    unsigned char N_divbybase  (struct N* m){
        unsigned char d=m->n0.v==0LL;
        struct NODEi64* n0=&m->n0;
        struct NODEi64* n1=n0->r;
        if(n1){
            n0->v=n1->v;
            struct NODEi64* n2=n1->r;
            if(n0->r=n2)n2->l=n0;
            else        m->np=n0;
            free(n1);
        }else n0->v=0LL;
        return d;
    }
    unsigned char N_half       (struct N* m){
        long long r=0LL;
        long long dgt;
        struct NODEi64* nm=m->np;
        do{ nm->v=dgt=(r=N_base*r+nm->v)/2LL;
            r-=dgt*2LL;
        }while(nm=nm->l);
        if(m->np->v==0LL&m->n0.r!=NULL){
            m->np=m->np->l;
            free(m->np->r);//deletable
            m->np->r=NULL;
        }
        return r==0LL;
    }
    unsigned char N_divby      (struct N* m,struct N* n){
        struct N* q=N_div(m,n);
        free$NODEi64$(m->n0.r);
        *m=*q;
        free(q);
        unsigned char d=N_divisibility(m);
        m->n0.l=NULL;
        printf("aaaa");
        return d;
    }
    struct N*     N_div        (struct N* m,struct N* n){
        struct N*  q=malloc(sizeof$N$);
        struct N* nq=malloc(sizeof$N$);
        ( q->np=& q->n0)->v=
        (nq->np=&nq->n0)->v=0LL;
                  q->n0 . l=
                  q->n0 . r=
                 nq->n0 . l=
                 nq->n0 . r=NULL;
        long long order=N_order(nq,m);

        struct N*  q_step=N_clone(m);
        struct N* nq_step;
        while(
            q_step->n0.r!=NULL|
            q_step->n0.v>1LL
        ){  if(order<0LL){
                N_addby  ( q, q_step);
                N_addby  (nq,nq_step=N_mult(n,q_step));
            }else if(order>0LL){
                N_minusby( q, q_step);
                N_minusby(nq,nq_step=N_mult(n,q_step));
            }else{
                free$N$( q_step);//deletable
                return q;
            }
            free$N$(nq_step);
            N_half(q_step);
            order=N_order(nq,m);
        }
        free$N$(q_step);//deletable

        while(order<0LL){
            N_addby1  ( q)  ;
            N_addby   (nq,n);
            order=N_order(nq,m);
        }
        while(order>0LL){
            N_minusby1( q)  ;
            N_minusby (nq,n);
            order=N_order(nq,m);
        }

        free$N$(nq);//deletable
        if(order)
            q->n0.l=(void*)1;
        return q;
    }
    unsigned char N_divisibility(struct N* q){
        unsigned char d=q->n0.l==NULL;
        q->n0.l=NULL;
        return d;
    }



    void print(struct N* x){
        struct NODEi64* n=x->np;
        printf("%lld",n->v);
        while((n=n->l))
            printf("%lld",n->v);
    }
    void println(struct N* x){
        print(x);
        printf("\n");
    }
//==============================================================================