@Composable
fun IdentityCard(
name: String,
idNumber: String,
dateOfBirth: String,
profileImage: Painter,
) {
Card(
modifier = Modifier
.fillMaxWidth()
.padding(16.dp)
.height(200.dp),
shape = RoundedCornerShape(16.dp),
elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
colors = CardDefaults.cardColors(containerColor = Color(0xFFf0f0f0))
) {
Row(
modifier = Modifier
.fillMaxSize()
.padding(16.dp),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
// Profile Picture
Image(
painter = profileImage,
contentDescription = "Profile Image",
modifier = Modifier
.size(80.dp)
.clip(CircleShape)
.border(2.dp, Color.Gray, CircleShape),
contentScale = ContentScale.Crop
)

            Spacer(modifier = Modifier.width(16.dp))

            // Identity Info
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = "ID: $idNumber", style = MaterialTheme.typography.bodyMedium)
                Text(text = "DOB: $dateOfBirth", style = MaterialTheme.typography.bodySmall)
            }

            // Barcode / QR Placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.DarkGray, shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "QR",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

