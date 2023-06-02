package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private List<Item> itemList;

    public ItemsAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nombreTarjeta;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenTarjeta);
            nombreTarjeta = itemView.findViewById(R.id.nombre_tarjeta);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);

        if (item.getTipo().equals("payment")) {
            //OBTENER TIPO DE TARJETA
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String email = currentUser.getEmail();
            DocumentReference userRef = db.collection("user").document(email).collection("payment").document(item.getId());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String numero_tarjeta = String.valueOf(document.get("cardNumber"));
                            char firstNumberIssuer = numero_tarjeta.charAt(0);
                            switch (firstNumberIssuer) {
                                case '2':
                                case '5':
                                    holder.imageView.setImageResource(R.drawable.mastercard_logo);
                                    break;
                                case '3':
                                    holder.imageView.setImageResource(R.drawable.amex_logo);
                                    break;
                                case '4':
                                    holder.imageView.setImageResource(R.drawable.visa_logo);
                                    break;
                                case '6':
                                    holder.imageView.setImageResource(R.drawable.discover_logo);
                                    break;
                                case '8':
                                    holder.imageView.setImageResource(R.drawable.unionpay_logo);
                                    break;
                                default:
                                    holder.imageView.setImageResource(R.drawable.piccreditcard);
                            }
                        }
                    }
                }
            });
        } else {
            if(item.getImageViewUri() == null){
                switch(item.getTipo()){
                    case "loyalty":
                        holder.imageView.setImageResource(R.drawable.picgiftcard);
                        break;
                    case "access":
                        holder.imageView.setImageResource(R.drawable.picaccesscard);
                        break;
                    case "transport":
                        holder.imageView.setImageResource(R.drawable.pictransportcard);
                        break;
                    case "custom":
                        holder.imageView.setImageResource(R.drawable.piccustomcard);
                        break;
                    default:
                        holder.imageView.setImageResource(R.drawable.picdnicard);
                }
            }else {
                Picasso.get().load(item.getImageViewUri()).into(holder.imageView);
            }
        }
        holder.nombreTarjeta.setText(item.getTitulo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();

                Intent intent = null;

                switch (item.getTipo()) {
                    case "loyalty":
                        intent = new Intent(context, SeleccionarGift.class);
                        break;
                    case "dni":
                        intent = new Intent(context, SeleccionarDni.class);
                        break;
                    case "payment":
                        intent = new Intent(context, SeleccionarCredit.class);
                        break;
                    case "transport":
                        intent = new Intent(context, SeleccionarTransport.class);
                        break;
                    case "access":
                        //intent = new Intent(context, SeleccionarTransport.class);
                        break;
                    case "custom":
                        intent = new Intent(context, SeleccionarCustom.class);
                        break;
                }

                intent.putExtra("itemId", item.getId());

                if (item.getImageViewUri() != null) {
                    intent.putExtra("imageUri", item.getImageViewUri().toString());
                } else {
                    intent.putExtra("imageUri", "nada");
                }

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
