--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.4

-- Started on 2025-04-01 15:35:39

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 255 (class 1255 OID 41461)
-- Name: check_stock(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_stock() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF (SELECT stock FROM Medicament WHERE DCI = NEW.medicament_dci) < NEW.qte_vendue THEN
        RAISE EXCEPTION 'Stock insuffisant pour ce médicament';
    END IF;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_stock() OWNER TO postgres;

--
-- TOC entry 256 (class 1255 OID 41463)
-- Name: update_stock(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_stock() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE Medicament
    SET stock = stock - NEW.qte_vendue
    WHERE DCI = NEW.medicament_dci;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_stock() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 247 (class 1259 OID 41594)
-- Name: bilanfinancier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bilanfinancier (
    id_bilan integer NOT NULL,
    date_debut date NOT NULL,
    date_fin date NOT NULL,
    total_ventes numeric(10,2) NOT NULL,
    total_achats numeric(10,2) NOT NULL,
    benefice_net numeric(10,2) NOT NULL,
    CONSTRAINT bilanfinancier_benefice_net_check CHECK ((benefice_net >= (0)::numeric)),
    CONSTRAINT bilanfinancier_total_achats_check CHECK ((total_achats >= (0)::numeric)),
    CONSTRAINT bilanfinancier_total_ventes_check CHECK ((total_ventes >= (0)::numeric)),
    CONSTRAINT chk_benefice_net CHECK ((benefice_net >= (0)::numeric))
);


ALTER TABLE public.bilanfinancier OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 41593)
-- Name: bilanfinancier_id_bilan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bilanfinancier_id_bilan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bilanfinancier_id_bilan_seq OWNER TO postgres;

--
-- TOC entry 5085 (class 0 OID 0)
-- Dependencies: 246
-- Name: bilanfinancier_id_bilan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bilanfinancier_id_bilan_seq OWNED BY public.bilanfinancier.id_bilan;


--
-- TOC entry 221 (class 1259 OID 41282)
-- Name: client; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client (
    client_id integer NOT NULL,
    nom character varying(255),
    prenom character varying(255),
    civilite character varying(50),
    ddn date
);


ALTER TABLE public.client OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 41281)
-- Name: client_client_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.client_client_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.client_client_id_seq OWNER TO postgres;

--
-- TOC entry 5087 (class 0 OID 0)
-- Dependencies: 220
-- Name: client_client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.client_client_id_seq OWNED BY public.client.client_id;


--
-- TOC entry 219 (class 1259 OID 41275)
-- Name: commande_livraison; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commande_livraison (
    num_commande integer NOT NULL,
    date_commande date,
    date_livraison date,
    nobondelivraisonfrn character varying(255)
);


ALTER TABLE public.commande_livraison OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 41274)
-- Name: commande_livraison_num_commande_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commande_livraison_num_commande_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commande_livraison_num_commande_seq OWNER TO postgres;

--
-- TOC entry 5090 (class 0 OID 0)
-- Dependencies: 218
-- Name: commande_livraison_num_commande_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commande_livraison_num_commande_seq OWNED BY public.commande_livraison.num_commande;


--
-- TOC entry 241 (class 1259 OID 41529)
-- Name: commandefournisseur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commandefournisseur (
    num_commande integer NOT NULL,
    fournisseur character varying(255) NOT NULL,
    medicament character varying(255),
    quantite integer NOT NULL,
    prix_unitaire double precision NOT NULL,
    statut character varying(50) DEFAULT 'en attente'::character varying,
    date_commande timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    quantite_recue integer DEFAULT 0,
    montant_total numeric(10,2),
    CONSTRAINT chk_montant_total_fournisseur CHECK ((montant_total >= (0)::numeric)),
    CONSTRAINT commandefournisseur_prix_unitaire_check CHECK ((prix_unitaire > (0)::double precision)),
    CONSTRAINT commandefournisseur_quantite_check CHECK ((quantite > 0)),
    CONSTRAINT commandefournisseur_statut_check CHECK (((statut)::text = ANY ((ARRAY['en attente'::character varying, 'reçue'::character varying, 'livrée'::character varying, 'annulée'::character varying])::text[])))
);


ALTER TABLE public.commandefournisseur OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 41528)
-- Name: commandefournisseur_num_commande_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commandefournisseur_num_commande_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commandefournisseur_num_commande_seq OWNER TO postgres;

--
-- TOC entry 5093 (class 0 OID 0)
-- Dependencies: 240
-- Name: commandefournisseur_num_commande_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commandefournisseur_num_commande_seq OWNED BY public.commandefournisseur.num_commande;


--
-- TOC entry 217 (class 1259 OID 41251)
-- Name: famille; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.famille (
    num_famille integer NOT NULL,
    nom_famille character varying(255)
);


ALTER TABLE public.famille OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 41250)
-- Name: famille_num_famille_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.famille_num_famille_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.famille_num_famille_seq OWNER TO postgres;

--
-- TOC entry 5096 (class 0 OID 0)
-- Dependencies: 216
-- Name: famille_num_famille_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.famille_num_famille_seq OWNED BY public.famille.num_famille;


--
-- TOC entry 215 (class 1259 OID 41245)
-- Name: forme; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.forme (
    nom_forme character varying(255) NOT NULL
);


ALTER TABLE public.forme OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 41518)
-- Name: fournisseur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fournisseur (
    id_fournisseur integer NOT NULL,
    nom character varying(255) NOT NULL,
    contact character varying(255),
    adresse text DEFAULT 'Adresse inconnue'::text NOT NULL,
    telephone character varying(20)
);


ALTER TABLE public.fournisseur OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 41517)
-- Name: fournisseur_id_fournisseur_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.fournisseur_id_fournisseur_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fournisseur_id_fournisseur_seq OWNER TO postgres;

--
-- TOC entry 5100 (class 0 OID 0)
-- Dependencies: 238
-- Name: fournisseur_id_fournisseur_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fournisseur_id_fournisseur_seq OWNED BY public.fournisseur.id_fournisseur;


--
-- TOC entry 227 (class 1259 OID 41360)
-- Name: ligne_cmd_livraison; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_cmd_livraison (
    ligne_cmd_id integer NOT NULL,
    qte_cmd integer,
    qte_livree integer,
    num_commande integer,
    medicament_dci character varying(255)
);


ALTER TABLE public.ligne_cmd_livraison OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 41359)
-- Name: ligne_cmd_livraison_ligne_cmd_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ligne_cmd_livraison_ligne_cmd_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ligne_cmd_livraison_ligne_cmd_id_seq OWNER TO postgres;

--
-- TOC entry 5103 (class 0 OID 0)
-- Dependencies: 226
-- Name: ligne_cmd_livraison_ligne_cmd_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ligne_cmd_livraison_ligne_cmd_id_seq OWNED BY public.ligne_cmd_livraison.ligne_cmd_id;


--
-- TOC entry 233 (class 1259 OID 41431)
-- Name: ligne_vendu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_vendu (
    id integer NOT NULL,
    num_vente integer,
    medicament_dci character varying(255),
    prixunit_vente double precision NOT NULL,
    qte_vendue integer NOT NULL
);


ALTER TABLE public.ligne_vendu OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 41430)
-- Name: ligne_vendu_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ligne_vendu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ligne_vendu_id_seq OWNER TO postgres;

--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 232
-- Name: ligne_vendu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ligne_vendu_id_seq OWNED BY public.ligne_vendu.id;


--
-- TOC entry 253 (class 1259 OID 41641)
-- Name: ligneordonnance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligneordonnance (
    id_ligne integer NOT NULL,
    id_ordonnance integer NOT NULL,
    medicament_dci character varying(100) NOT NULL,
    quantite integer NOT NULL,
    prix_unitaire numeric(10,2) NOT NULL,
    CONSTRAINT ligneordonnance_prix_unitaire_check CHECK ((prix_unitaire >= (0)::numeric)),
    CONSTRAINT ligneordonnance_quantite_check CHECK ((quantite > 0))
);


ALTER TABLE public.ligneordonnance OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 41640)
-- Name: ligneordonnance_id_ligne_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ligneordonnance_id_ligne_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ligneordonnance_id_ligne_seq OWNER TO postgres;

--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 252
-- Name: ligneordonnance_id_ligne_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ligneordonnance_id_ligne_seq OWNED BY public.ligneordonnance.id_ligne;


--
-- TOC entry 231 (class 1259 OID 41415)
-- Name: medicament; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.medicament (
    dci character varying(255) NOT NULL,
    famille character varying(100),
    prix_vente double precision NOT NULL,
    stock integer NOT NULL,
    seuil_min integer NOT NULL,
    stock_max integer NOT NULL,
    ordonnance_requise boolean DEFAULT false
);


ALTER TABLE public.medicament OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 41570)
-- Name: mouvementstock; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mouvementstock (
    id integer NOT NULL,
    medicament_dci character varying(255),
    type_mouvement character varying(50),
    quantite integer NOT NULL,
    date_mouvement timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    raison character varying(255),
    CONSTRAINT mouvementstock_quantite_check CHECK ((quantite > 0)),
    CONSTRAINT mouvementstock_type_mouvement_check CHECK (((type_mouvement)::text = ANY ((ARRAY['ENTREE'::character varying, 'SORTIE'::character varying])::text[])))
);


ALTER TABLE public.mouvementstock OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 41569)
-- Name: mouvementstock_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mouvementstock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.mouvementstock_id_seq OWNER TO postgres;

--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 244
-- Name: mouvementstock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mouvementstock_id_seq OWNED BY public.mouvementstock.id;


--
-- TOC entry 251 (class 1259 OID 41634)
-- Name: ordonnance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ordonnance (
    id_ordonnance integer NOT NULL,
    nom_medecin character varying(100) NOT NULL,
    nom_patient character varying(100) NOT NULL,
    date_prescription date NOT NULL
);


ALTER TABLE public.ordonnance OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 41633)
-- Name: ordonnance_id_ordonnance_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ordonnance_id_ordonnance_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ordonnance_id_ordonnance_seq OWNER TO postgres;

--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 250
-- Name: ordonnance_id_ordonnance_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ordonnance_id_ordonnance_seq OWNED BY public.ordonnance.id_ordonnance;


--
-- TOC entry 225 (class 1259 OID 41336)
-- Name: permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.permission (
    permission_id integer NOT NULL,
    nom_permission character varying(255)
);


ALTER TABLE public.permission OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 41335)
-- Name: permission_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.permission_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.permission_permission_id_seq OWNER TO postgres;

--
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 224
-- Name: permission_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.permission_permission_id_seq OWNED BY public.permission.permission_id;


--
-- TOC entry 235 (class 1259 OID 41448)
-- Name: prescription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prescription (
    id_prescription integer NOT NULL,
    num_vente integer,
    nom_medecin character varying(255) NOT NULL,
    date_prescription date NOT NULL,
    nom_patient character varying(255) NOT NULL
);


ALTER TABLE public.prescription OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 41447)
-- Name: prescription_id_prescription_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prescription_id_prescription_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prescription_id_prescription_seq OWNER TO postgres;

--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 234
-- Name: prescription_id_prescription_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prescription_id_prescription_seq OWNED BY public.prescription.id_prescription;


--
-- TOC entry 249 (class 1259 OID 41607)
-- Name: prixfournisseur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prixfournisseur (
    id integer NOT NULL,
    id_fournisseur integer,
    dci character varying(255),
    prix_achat numeric(10,2) NOT NULL,
    date_mise_a_jour date DEFAULT CURRENT_DATE,
    CONSTRAINT prixfournisseur_prix_achat_check CHECK ((prix_achat >= (0)::numeric))
);


ALTER TABLE public.prixfournisseur OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 41606)
-- Name: prixfournisseur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prixfournisseur_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prixfournisseur_id_seq OWNER TO postgres;

--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 248
-- Name: prixfournisseur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prixfournisseur_id_seq OWNED BY public.prixfournisseur.id;


--
-- TOC entry 243 (class 1259 OID 41554)
-- Name: problemesreception; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.problemesreception (
    id integer NOT NULL,
    num_commande integer NOT NULL,
    medicament character varying(255) NOT NULL,
    description text NOT NULL,
    date_signalement timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.problemesreception OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 41553)
-- Name: problemesreception_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.problemesreception_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.problemesreception_id_seq OWNER TO postgres;

--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 242
-- Name: problemesreception_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.problemesreception_id_seq OWNED BY public.problemesreception.id;


--
-- TOC entry 254 (class 1259 OID 41660)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    username character varying(100) NOT NULL,
    mdp character varying(100) NOT NULL,
    email character varying(250),
    status character varying(100)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 41303)
-- Name: utilisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateur (
    utilisateur_id integer NOT NULL,
    prenom character varying(255),
    nom character varying(255),
    date_naissance date,
    telephone character varying(20),
    email character varying(255),
    adresse character varying(255),
    identifiant character varying(255),
    mot_de_passe character varying(255),
    status character varying(50),
    est_superadmin boolean DEFAULT false,
    dernier_login timestamp without time zone
);


ALTER TABLE public.utilisateur OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 41376)
-- Name: utilisateur_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateur_permission (
    utilisateur_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.utilisateur_permission OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 41302)
-- Name: utilisateur_utilisateur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utilisateur_utilisateur_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utilisateur_utilisateur_id_seq OWNER TO postgres;

--
-- TOC entry 5123 (class 0 OID 0)
-- Dependencies: 222
-- Name: utilisateur_utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateur_utilisateur_id_seq OWNED BY public.utilisateur.utilisateur_id;


--
-- TOC entry 230 (class 1259 OID 41402)
-- Name: vendre; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendre (
    id_vendre integer NOT NULL,
    num_vente integer NOT NULL,
    medicament_dci character varying(255) NOT NULL,
    montant_total numeric(10,2) NOT NULL,
    statut character varying(50) DEFAULT 'en attente'::character varying,
    date_vente date DEFAULT CURRENT_DATE
);


ALTER TABLE public.vendre OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 41401)
-- Name: vendre_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vendre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vendre_id_seq OWNER TO postgres;

--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 229
-- Name: vendre_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vendre_id_seq OWNED BY public.vendre.id_vendre;


--
-- TOC entry 237 (class 1259 OID 41466)
-- Name: vente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vente (
    num_vente integer NOT NULL,
    medicament character varying(255),
    montant_total numeric(10,2),
    statut character varying(255),
    date_vente date,
    CONSTRAINT chk_montant_total CHECK ((montant_total >= (0)::numeric))
);


ALTER TABLE public.vente OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 41465)
-- Name: vente_nouvelle_num_vente_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vente_nouvelle_num_vente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vente_nouvelle_num_vente_seq OWNER TO postgres;

--
-- TOC entry 5129 (class 0 OID 0)
-- Dependencies: 236
-- Name: vente_nouvelle_num_vente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vente_nouvelle_num_vente_seq OWNED BY public.vente.num_vente;


--
-- TOC entry 4815 (class 2604 OID 41597)
-- Name: bilanfinancier id_bilan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bilanfinancier ALTER COLUMN id_bilan SET DEFAULT nextval('public.bilanfinancier_id_bilan_seq'::regclass);


--
-- TOC entry 4793 (class 2604 OID 41285)
-- Name: client client_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client ALTER COLUMN client_id SET DEFAULT nextval('public.client_client_id_seq'::regclass);


--
-- TOC entry 4792 (class 2604 OID 41278)
-- Name: commande_livraison num_commande; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commande_livraison ALTER COLUMN num_commande SET DEFAULT nextval('public.commande_livraison_num_commande_seq'::regclass);


--
-- TOC entry 4807 (class 2604 OID 41532)
-- Name: commandefournisseur num_commande; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commandefournisseur ALTER COLUMN num_commande SET DEFAULT nextval('public.commandefournisseur_num_commande_seq'::regclass);


--
-- TOC entry 4791 (class 2604 OID 41254)
-- Name: famille num_famille; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.famille ALTER COLUMN num_famille SET DEFAULT nextval('public.famille_num_famille_seq'::regclass);


--
-- TOC entry 4805 (class 2604 OID 41521)
-- Name: fournisseur id_fournisseur; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseur ALTER COLUMN id_fournisseur SET DEFAULT nextval('public.fournisseur_id_fournisseur_seq'::regclass);


--
-- TOC entry 4797 (class 2604 OID 41363)
-- Name: ligne_cmd_livraison ligne_cmd_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_cmd_livraison ALTER COLUMN ligne_cmd_id SET DEFAULT nextval('public.ligne_cmd_livraison_ligne_cmd_id_seq'::regclass);


--
-- TOC entry 4802 (class 2604 OID 41434)
-- Name: ligne_vendu id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vendu ALTER COLUMN id SET DEFAULT nextval('public.ligne_vendu_id_seq'::regclass);


--
-- TOC entry 4819 (class 2604 OID 41644)
-- Name: ligneordonnance id_ligne; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligneordonnance ALTER COLUMN id_ligne SET DEFAULT nextval('public.ligneordonnance_id_ligne_seq'::regclass);


--
-- TOC entry 4813 (class 2604 OID 41573)
-- Name: mouvementstock id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mouvementstock ALTER COLUMN id SET DEFAULT nextval('public.mouvementstock_id_seq'::regclass);


--
-- TOC entry 4818 (class 2604 OID 41637)
-- Name: ordonnance id_ordonnance; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ordonnance ALTER COLUMN id_ordonnance SET DEFAULT nextval('public.ordonnance_id_ordonnance_seq'::regclass);


--
-- TOC entry 4796 (class 2604 OID 41339)
-- Name: permission permission_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permission ALTER COLUMN permission_id SET DEFAULT nextval('public.permission_permission_id_seq'::regclass);


--
-- TOC entry 4803 (class 2604 OID 41451)
-- Name: prescription id_prescription; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription ALTER COLUMN id_prescription SET DEFAULT nextval('public.prescription_id_prescription_seq'::regclass);


--
-- TOC entry 4816 (class 2604 OID 41610)
-- Name: prixfournisseur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prixfournisseur ALTER COLUMN id SET DEFAULT nextval('public.prixfournisseur_id_seq'::regclass);


--
-- TOC entry 4811 (class 2604 OID 41557)
-- Name: problemesreception id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.problemesreception ALTER COLUMN id SET DEFAULT nextval('public.problemesreception_id_seq'::regclass);


--
-- TOC entry 4794 (class 2604 OID 41306)
-- Name: utilisateur utilisateur_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur ALTER COLUMN utilisateur_id SET DEFAULT nextval('public.utilisateur_utilisateur_id_seq'::regclass);


--
-- TOC entry 4798 (class 2604 OID 41405)
-- Name: vendre id_vendre; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendre ALTER COLUMN id_vendre SET DEFAULT nextval('public.vendre_id_seq'::regclass);


--
-- TOC entry 4804 (class 2604 OID 41469)
-- Name: vente num_vente; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente ALTER COLUMN num_vente SET DEFAULT nextval('public.vente_nouvelle_num_vente_seq'::regclass);


--
-- TOC entry 5072 (class 0 OID 41594)
-- Dependencies: 247
-- Data for Name: bilanfinancier; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bilanfinancier (id_bilan, date_debut, date_fin, total_ventes, total_achats, benefice_net) FROM stdin;
\.


--
-- TOC entry 5046 (class 0 OID 41282)
-- Dependencies: 221
-- Data for Name: client; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.client (client_id, nom, prenom, civilite, ddn) FROM stdin;
\.


--
-- TOC entry 5044 (class 0 OID 41275)
-- Dependencies: 219
-- Data for Name: commande_livraison; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commande_livraison (num_commande, date_commande, date_livraison, nobondelivraisonfrn) FROM stdin;
\.


--
-- TOC entry 5066 (class 0 OID 41529)
-- Dependencies: 241
-- Data for Name: commandefournisseur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commandefournisseur (num_commande, fournisseur, medicament, quantite, prix_unitaire, statut, date_commande, quantite_recue, montant_total) FROM stdin;
3	PharmaSupply	Ibuprofène	5	5.99	en attente	2025-02-04 10:12:02.062212	0	\N
2	PharmaSupply	Paracetamol	100	2.5	reçue	2025-02-04 09:54:00.982265	100	250.00
5	MedDistrib	Ibuprofène	10	5.99	livrée	2025-02-05 20:56:40.677827	0	59.90
6	MedDistrib	Ibuprofène	15	5.99	reçue	2025-02-07 13:43:25.603479	15	89.85
7	MedDistrib	Paracetamol	5	5.99	reçue	2025-02-07 20:12:43.645169	5	29.95
4	MedDistrib	Paracetamol	10	5.99	reçue	2025-02-04 11:44:59.177179	10	\N
8	MedDistrib	Paracetamol	6	5.99	reçue	2025-03-28 06:06:31.903776	6	35.94
\.


--
-- TOC entry 5042 (class 0 OID 41251)
-- Dependencies: 217
-- Data for Name: famille; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.famille (num_famille, nom_famille) FROM stdin;
1	analgésique
2	antibiotique
\.


--
-- TOC entry 5040 (class 0 OID 41245)
-- Dependencies: 215
-- Data for Name: forme; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.forme (nom_forme) FROM stdin;
comprimé
sirop
\.


--
-- TOC entry 5064 (class 0 OID 41518)
-- Dependencies: 239
-- Data for Name: fournisseur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.fournisseur (id_fournisseur, nom, contact, adresse, telephone) FROM stdin;
4	MedDistrib	info@meddistrib.com	Adresse inconnue	Inconnu
3	PharmaSupply	contact@pharmasupply.com	63 rue frederic et irene joliot curie	0787651432
\.


--
-- TOC entry 5052 (class 0 OID 41360)
-- Dependencies: 227
-- Data for Name: ligne_cmd_livraison; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligne_cmd_livraison (ligne_cmd_id, qte_cmd, qte_livree, num_commande, medicament_dci) FROM stdin;
\.


--
-- TOC entry 5058 (class 0 OID 41431)
-- Dependencies: 233
-- Data for Name: ligne_vendu; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligne_vendu (id, num_vente, medicament_dci, prixunit_vente, qte_vendue) FROM stdin;
5	2	Paracetamol	5.99	25
6	3	Paracetamol	5.99	8
7	4	Paracetamol	5.99	7
8	5	Paracetamol	5.99	5
9	6	Paracetamol	5.99	1
10	7	Ibuprofène	7.5	23
11	7	Paracetamol	5.99	2
12	8	Paracetamol	5.99	5
13	9	Amoxicilline	4.8	4
14	10	Paracetamol	5.99	3
15	11	Amoxicilline	4.8	10
16	12	Paracetamol	5.99	5
17	13	Ibuprofène	5.99	5
\.


--
-- TOC entry 5078 (class 0 OID 41641)
-- Dependencies: 253
-- Data for Name: ligneordonnance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligneordonnance (id_ligne, id_ordonnance, medicament_dci, quantite, prix_unitaire) FROM stdin;
\.


--
-- TOC entry 5056 (class 0 OID 41415)
-- Dependencies: 231
-- Data for Name: medicament; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.medicament (dci, famille, prix_vente, stock, seuil_min, stock_max, ordonnance_requise) FROM stdin;
Amoxicilline	Antibiotique	4.8	136	20	600	t
Ibuprofène	Anti-inflammatoire	5.99	160	20	1000	f
Paracetamol	Antalgique	5.99	17	10	500	f
\.


--
-- TOC entry 5070 (class 0 OID 41570)
-- Dependencies: 245
-- Data for Name: mouvementstock; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mouvementstock (id, medicament_dci, type_mouvement, quantite, date_mouvement, raison) FROM stdin;
\.


--
-- TOC entry 5076 (class 0 OID 41634)
-- Dependencies: 251
-- Data for Name: ordonnance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ordonnance (id_ordonnance, nom_medecin, nom_patient, date_prescription) FROM stdin;
\.


--
-- TOC entry 5050 (class 0 OID 41336)
-- Dependencies: 225
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.permission (permission_id, nom_permission) FROM stdin;
\.


--
-- TOC entry 5060 (class 0 OID 41448)
-- Dependencies: 235
-- Data for Name: prescription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.prescription (id_prescription, num_vente, nom_medecin, date_prescription, nom_patient) FROM stdin;
\.


--
-- TOC entry 5074 (class 0 OID 41607)
-- Dependencies: 249
-- Data for Name: prixfournisseur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.prixfournisseur (id, id_fournisseur, dci, prix_achat, date_mise_a_jour) FROM stdin;
\.


--
-- TOC entry 5068 (class 0 OID 41554)
-- Dependencies: 243
-- Data for Name: problemesreception; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.problemesreception (id, num_commande, medicament, description, date_signalement) FROM stdin;
\.


--
-- TOC entry 5079 (class 0 OID 41660)
-- Dependencies: 254
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (username, mdp, email, status) FROM stdin;
liwell	1234	liwell@gmail.com	Administrateur
caissier	1234	csv@goat.fr	Caissier
vendeur	1234	vd@goat.fr	Vendeur
pharmacien	1234	pm@goat.fr	Pharmacien
admin	1234	ad@goat.fr	Administrateur
\.


--
-- TOC entry 5048 (class 0 OID 41303)
-- Dependencies: 223
-- Data for Name: utilisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur (utilisateur_id, prenom, nom, date_naissance, telephone, email, adresse, identifiant, mot_de_passe, status, est_superadmin, dernier_login) FROM stdin;
\.


--
-- TOC entry 5053 (class 0 OID 41376)
-- Dependencies: 228
-- Data for Name: utilisateur_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur_permission (utilisateur_id, permission_id) FROM stdin;
\.


--
-- TOC entry 5055 (class 0 OID 41402)
-- Dependencies: 230
-- Data for Name: vendre; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vendre (id_vendre, num_vente, medicament_dci, montant_total, statut, date_vente) FROM stdin;
\.


--
-- TOC entry 5062 (class 0 OID 41466)
-- Dependencies: 237
-- Data for Name: vente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vente (num_vente, medicament, montant_total, statut, date_vente) FROM stdin;
3	\N	47.92	en attente	2025-02-03
4	Paracetamol	41.93	en attente	2025-02-03
5	Paracetamol	29.95	payée	2025-02-03
6	Paracetamol	5.99	en attente	2025-02-03
7	Ibuprofène, Paracetamol	184.48	en attente	2025-02-03
2	\N	149.75	payée	2025-02-03
8	Paracetamol	29.95	payée	2025-02-07
9	Amoxicilline	19.20	payée	2025-02-07
10	Paracetamol	17.97	payée	2025-02-09
11	Amoxicilline	48.00	en attente	2025-02-09
12	Paracetamol	29.95	payée	2025-03-17
13	Ibuprofène	29.95	payée	2025-03-28
\.


--
-- TOC entry 5131 (class 0 OID 0)
-- Dependencies: 246
-- Name: bilanfinancier_id_bilan_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bilanfinancier_id_bilan_seq', 1, false);


--
-- TOC entry 5132 (class 0 OID 0)
-- Dependencies: 220
-- Name: client_client_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.client_client_id_seq', 1, false);


--
-- TOC entry 5133 (class 0 OID 0)
-- Dependencies: 218
-- Name: commande_livraison_num_commande_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commande_livraison_num_commande_seq', 1, false);


--
-- TOC entry 5134 (class 0 OID 0)
-- Dependencies: 240
-- Name: commandefournisseur_num_commande_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commandefournisseur_num_commande_seq', 8, true);


--
-- TOC entry 5135 (class 0 OID 0)
-- Dependencies: 216
-- Name: famille_num_famille_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.famille_num_famille_seq', 2, true);


--
-- TOC entry 5136 (class 0 OID 0)
-- Dependencies: 238
-- Name: fournisseur_id_fournisseur_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fournisseur_id_fournisseur_seq', 4, true);


--
-- TOC entry 5137 (class 0 OID 0)
-- Dependencies: 226
-- Name: ligne_cmd_livraison_ligne_cmd_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ligne_cmd_livraison_ligne_cmd_id_seq', 1, false);


--
-- TOC entry 5138 (class 0 OID 0)
-- Dependencies: 232
-- Name: ligne_vendu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ligne_vendu_id_seq', 17, true);


--
-- TOC entry 5139 (class 0 OID 0)
-- Dependencies: 252
-- Name: ligneordonnance_id_ligne_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ligneordonnance_id_ligne_seq', 1, false);


--
-- TOC entry 5140 (class 0 OID 0)
-- Dependencies: 244
-- Name: mouvementstock_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mouvementstock_id_seq', 1, false);


--
-- TOC entry 5141 (class 0 OID 0)
-- Dependencies: 250
-- Name: ordonnance_id_ordonnance_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ordonnance_id_ordonnance_seq', 1, false);


--
-- TOC entry 5142 (class 0 OID 0)
-- Dependencies: 224
-- Name: permission_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.permission_permission_id_seq', 1, false);


--
-- TOC entry 5143 (class 0 OID 0)
-- Dependencies: 234
-- Name: prescription_id_prescription_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prescription_id_prescription_seq', 1, false);


--
-- TOC entry 5144 (class 0 OID 0)
-- Dependencies: 248
-- Name: prixfournisseur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prixfournisseur_id_seq', 1, false);


--
-- TOC entry 5145 (class 0 OID 0)
-- Dependencies: 242
-- Name: problemesreception_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.problemesreception_id_seq', 2, true);


--
-- TOC entry 5146 (class 0 OID 0)
-- Dependencies: 222
-- Name: utilisateur_utilisateur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateur_utilisateur_id_seq', 1, false);


--
-- TOC entry 5147 (class 0 OID 0)
-- Dependencies: 229
-- Name: vendre_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vendre_id_seq', 1, true);


--
-- TOC entry 5148 (class 0 OID 0)
-- Dependencies: 236
-- Name: vente_nouvelle_num_vente_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vente_nouvelle_num_vente_seq', 13, true);


--
-- TOC entry 4873 (class 2606 OID 41602)
-- Name: bilanfinancier bilanfinancier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bilanfinancier
    ADD CONSTRAINT bilanfinancier_pkey PRIMARY KEY (id_bilan);


--
-- TOC entry 4841 (class 2606 OID 41289)
-- Name: client client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT client_pkey PRIMARY KEY (client_id);


--
-- TOC entry 4839 (class 2606 OID 41280)
-- Name: commande_livraison commande_livraison_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commande_livraison
    ADD CONSTRAINT commande_livraison_pkey PRIMARY KEY (num_commande);


--
-- TOC entry 4867 (class 2606 OID 41541)
-- Name: commandefournisseur commandefournisseur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commandefournisseur
    ADD CONSTRAINT commandefournisseur_pkey PRIMARY KEY (num_commande);


--
-- TOC entry 4837 (class 2606 OID 41256)
-- Name: famille famille_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.famille
    ADD CONSTRAINT famille_pkey PRIMARY KEY (num_famille);


--
-- TOC entry 4835 (class 2606 OID 41249)
-- Name: forme forme_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.forme
    ADD CONSTRAINT forme_pkey PRIMARY KEY (nom_forme);


--
-- TOC entry 4863 (class 2606 OID 41527)
-- Name: fournisseur fournisseur_nom_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseur
    ADD CONSTRAINT fournisseur_nom_key UNIQUE (nom);


--
-- TOC entry 4865 (class 2606 OID 41525)
-- Name: fournisseur fournisseur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseur
    ADD CONSTRAINT fournisseur_pkey PRIMARY KEY (id_fournisseur);


--
-- TOC entry 4849 (class 2606 OID 41365)
-- Name: ligne_cmd_livraison ligne_cmd_livraison_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_cmd_livraison
    ADD CONSTRAINT ligne_cmd_livraison_pkey PRIMARY KEY (ligne_cmd_id);


--
-- TOC entry 4857 (class 2606 OID 41436)
-- Name: ligne_vendu ligne_vendu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vendu
    ADD CONSTRAINT ligne_vendu_pkey PRIMARY KEY (id);


--
-- TOC entry 4879 (class 2606 OID 41648)
-- Name: ligneordonnance ligneordonnance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligneordonnance
    ADD CONSTRAINT ligneordonnance_pkey PRIMARY KEY (id_ligne);


--
-- TOC entry 4855 (class 2606 OID 41419)
-- Name: medicament medicament_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medicament
    ADD CONSTRAINT medicament_pkey PRIMARY KEY (dci);


--
-- TOC entry 4871 (class 2606 OID 41580)
-- Name: mouvementstock mouvementstock_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mouvementstock
    ADD CONSTRAINT mouvementstock_pkey PRIMARY KEY (id);


--
-- TOC entry 4877 (class 2606 OID 41639)
-- Name: ordonnance ordonnance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ordonnance
    ADD CONSTRAINT ordonnance_pkey PRIMARY KEY (id_ordonnance);


--
-- TOC entry 4847 (class 2606 OID 41341)
-- Name: permission permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (permission_id);


--
-- TOC entry 4859 (class 2606 OID 41455)
-- Name: prescription prescription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT prescription_pkey PRIMARY KEY (id_prescription);


--
-- TOC entry 4875 (class 2606 OID 41614)
-- Name: prixfournisseur prixfournisseur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prixfournisseur
    ADD CONSTRAINT prixfournisseur_pkey PRIMARY KEY (id);


--
-- TOC entry 4869 (class 2606 OID 41562)
-- Name: problemesreception problemesreception_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.problemesreception
    ADD CONSTRAINT problemesreception_pkey PRIMARY KEY (id);


--
-- TOC entry 4843 (class 2606 OID 41313)
-- Name: utilisateur utilisateur_identifiant_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_identifiant_key UNIQUE (identifiant);


--
-- TOC entry 4851 (class 2606 OID 41380)
-- Name: utilisateur_permission utilisateur_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur_permission
    ADD CONSTRAINT utilisateur_permission_pkey PRIMARY KEY (utilisateur_id, permission_id);


--
-- TOC entry 4845 (class 2606 OID 41311)
-- Name: utilisateur utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (utilisateur_id);


--
-- TOC entry 4853 (class 2606 OID 41409)
-- Name: vendre vendre_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendre
    ADD CONSTRAINT vendre_pkey PRIMARY KEY (id_vendre);


--
-- TOC entry 4861 (class 2606 OID 41473)
-- Name: vente vente_nouvelle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente
    ADD CONSTRAINT vente_nouvelle_pkey PRIMARY KEY (num_vente);


--
-- TOC entry 4895 (class 2620 OID 41462)
-- Name: ligne_vendu trg_check_stock; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_check_stock BEFORE INSERT ON public.ligne_vendu FOR EACH ROW EXECUTE FUNCTION public.check_stock();


--
-- TOC entry 4896 (class 2620 OID 41464)
-- Name: ligne_vendu trg_update_stock; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_update_stock AFTER INSERT ON public.ligne_vendu FOR EACH ROW EXECUTE FUNCTION public.update_stock();


--
-- TOC entry 4886 (class 2606 OID 41542)
-- Name: commandefournisseur commandefournisseur_medicament_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commandefournisseur
    ADD CONSTRAINT commandefournisseur_medicament_fkey FOREIGN KEY (medicament) REFERENCES public.medicament(dci) ON DELETE CASCADE;


--
-- TOC entry 4887 (class 2606 OID 41547)
-- Name: commandefournisseur fk_fournisseur; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commandefournisseur
    ADD CONSTRAINT fk_fournisseur FOREIGN KEY (fournisseur) REFERENCES public.fournisseur(nom) ON DELETE CASCADE;


--
-- TOC entry 4888 (class 2606 OID 41627)
-- Name: commandefournisseur fk_medicament; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commandefournisseur
    ADD CONSTRAINT fk_medicament FOREIGN KEY (medicament) REFERENCES public.medicament(dci);


--
-- TOC entry 4889 (class 2606 OID 41563)
-- Name: problemesreception fk_probleme_commande; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.problemesreception
    ADD CONSTRAINT fk_probleme_commande FOREIGN KEY (num_commande) REFERENCES public.commandefournisseur(num_commande) ON DELETE CASCADE;


--
-- TOC entry 4880 (class 2606 OID 41366)
-- Name: ligne_cmd_livraison ligne_cmd_livraison_num_commande_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_cmd_livraison
    ADD CONSTRAINT ligne_cmd_livraison_num_commande_fkey FOREIGN KEY (num_commande) REFERENCES public.commande_livraison(num_commande);


--
-- TOC entry 4883 (class 2606 OID 41442)
-- Name: ligne_vendu ligne_vendu_medicament_dci_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vendu
    ADD CONSTRAINT ligne_vendu_medicament_dci_fkey FOREIGN KEY (medicament_dci) REFERENCES public.medicament(dci) ON DELETE CASCADE;


--
-- TOC entry 4884 (class 2606 OID 41474)
-- Name: ligne_vendu ligne_vendu_num_vente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vendu
    ADD CONSTRAINT ligne_vendu_num_vente_fkey FOREIGN KEY (num_vente) REFERENCES public.vente(num_vente);


--
-- TOC entry 4893 (class 2606 OID 41649)
-- Name: ligneordonnance ligneordonnance_id_ordonnance_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligneordonnance
    ADD CONSTRAINT ligneordonnance_id_ordonnance_fkey FOREIGN KEY (id_ordonnance) REFERENCES public.ordonnance(id_ordonnance) ON DELETE CASCADE;


--
-- TOC entry 4894 (class 2606 OID 41654)
-- Name: ligneordonnance ligneordonnance_medicament_dci_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligneordonnance
    ADD CONSTRAINT ligneordonnance_medicament_dci_fkey FOREIGN KEY (medicament_dci) REFERENCES public.medicament(dci) ON DELETE CASCADE;


--
-- TOC entry 4890 (class 2606 OID 41581)
-- Name: mouvementstock mouvementstock_medicament_dci_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mouvementstock
    ADD CONSTRAINT mouvementstock_medicament_dci_fkey FOREIGN KEY (medicament_dci) REFERENCES public.medicament(dci) ON DELETE CASCADE;


--
-- TOC entry 4885 (class 2606 OID 41479)
-- Name: prescription prescription_num_vente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT prescription_num_vente_fkey FOREIGN KEY (num_vente) REFERENCES public.vente(num_vente);


--
-- TOC entry 4891 (class 2606 OID 41620)
-- Name: prixfournisseur prixfournisseur_dci_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prixfournisseur
    ADD CONSTRAINT prixfournisseur_dci_fkey FOREIGN KEY (dci) REFERENCES public.medicament(dci) ON DELETE CASCADE;


--
-- TOC entry 4892 (class 2606 OID 41615)
-- Name: prixfournisseur prixfournisseur_id_fournisseur_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prixfournisseur
    ADD CONSTRAINT prixfournisseur_id_fournisseur_fkey FOREIGN KEY (id_fournisseur) REFERENCES public.fournisseur(id_fournisseur) ON DELETE CASCADE;


--
-- TOC entry 4881 (class 2606 OID 41386)
-- Name: utilisateur_permission utilisateur_permission_permission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur_permission
    ADD CONSTRAINT utilisateur_permission_permission_id_fkey FOREIGN KEY (permission_id) REFERENCES public.permission(permission_id);


--
-- TOC entry 4882 (class 2606 OID 41381)
-- Name: utilisateur_permission utilisateur_permission_utilisateur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur_permission
    ADD CONSTRAINT utilisateur_permission_utilisateur_id_fkey FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(utilisateur_id);


--
-- TOC entry 5086 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE client; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.client TO admin;


--
-- TOC entry 5088 (class 0 OID 0)
-- Dependencies: 220
-- Name: SEQUENCE client_client_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.client_client_id_seq TO admin;


--
-- TOC entry 5089 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE commande_livraison; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.commande_livraison TO admin;


--
-- TOC entry 5091 (class 0 OID 0)
-- Dependencies: 218
-- Name: SEQUENCE commande_livraison_num_commande_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.commande_livraison_num_commande_seq TO admin;


--
-- TOC entry 5092 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE commandefournisseur; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.commandefournisseur TO admin;


--
-- TOC entry 5094 (class 0 OID 0)
-- Dependencies: 240
-- Name: SEQUENCE commandefournisseur_num_commande_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.commandefournisseur_num_commande_seq TO admin;


--
-- TOC entry 5095 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE famille; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.famille TO admin;


--
-- TOC entry 5097 (class 0 OID 0)
-- Dependencies: 216
-- Name: SEQUENCE famille_num_famille_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.famille_num_famille_seq TO admin;


--
-- TOC entry 5098 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE forme; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.forme TO admin;


--
-- TOC entry 5099 (class 0 OID 0)
-- Dependencies: 239
-- Name: TABLE fournisseur; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.fournisseur TO admin;


--
-- TOC entry 5101 (class 0 OID 0)
-- Dependencies: 238
-- Name: SEQUENCE fournisseur_id_fournisseur_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.fournisseur_id_fournisseur_seq TO admin;


--
-- TOC entry 5102 (class 0 OID 0)
-- Dependencies: 227
-- Name: TABLE ligne_cmd_livraison; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.ligne_cmd_livraison TO admin;


--
-- TOC entry 5104 (class 0 OID 0)
-- Dependencies: 226
-- Name: SEQUENCE ligne_cmd_livraison_ligne_cmd_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.ligne_cmd_livraison_ligne_cmd_id_seq TO admin;


--
-- TOC entry 5105 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE ligne_vendu; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.ligne_vendu TO admin;


--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 232
-- Name: SEQUENCE ligne_vendu_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.ligne_vendu_id_seq TO admin;


--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE medicament; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.medicament TO admin;


--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE permission; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.permission TO admin;


--
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 224
-- Name: SEQUENCE permission_permission_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.permission_permission_id_seq TO admin;


--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE prescription; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.prescription TO admin;


--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 234
-- Name: SEQUENCE prescription_id_prescription_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.prescription_id_prescription_seq TO admin;


--
-- TOC entry 5120 (class 0 OID 0)
-- Dependencies: 254
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.users TO admin;


--
-- TOC entry 5121 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE utilisateur; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.utilisateur TO admin;


--
-- TOC entry 5122 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE utilisateur_permission; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.utilisateur_permission TO admin;


--
-- TOC entry 5124 (class 0 OID 0)
-- Dependencies: 222
-- Name: SEQUENCE utilisateur_utilisateur_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.utilisateur_utilisateur_id_seq TO admin;


--
-- TOC entry 5125 (class 0 OID 0)
-- Dependencies: 230
-- Name: TABLE vendre; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.vendre TO admin;


--
-- TOC entry 5127 (class 0 OID 0)
-- Dependencies: 229
-- Name: SEQUENCE vendre_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.vendre_id_seq TO admin;


--
-- TOC entry 5128 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE vente; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.vente TO admin;


--
-- TOC entry 5130 (class 0 OID 0)
-- Dependencies: 236
-- Name: SEQUENCE vente_nouvelle_num_vente_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.vente_nouvelle_num_vente_seq TO admin;


-- Completed on 2025-04-01 15:35:39

--
-- PostgreSQL database dump complete
--

