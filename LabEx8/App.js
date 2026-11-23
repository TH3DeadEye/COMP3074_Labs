import React, { useEffect, useState } from 'react';

import {
  ActivityIndicator,
  FlatList,
  Platform,
  Pressable,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';

// one row in the user list
function UserItem({ user, onPress }) {
  return (
    <Pressable
      style={styles.userItem}
      onPress={onPress}
    >
      <Text style={styles.userName}>
        {user.name}
      </Text>

      <Text style={styles.userEmail}>
        {user.email}
      </Text>
    </Pressable>
  );
}

export default function App() {
  // app state
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // get the users once when the app starts
  useEffect(() => {
    const loadUsers = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await fetch(
          'https://jsonplaceholder.typicode.com/users'
        );

        if (!response.ok) {
          throw new Error(
            `HTTP error: ${response.status}`
          );
        }

        const data = await response.json();

        setUsers(data);

      } catch (err) {
        setError('Unable to load users.');

      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, []);

  // loading screen
  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
        <Text style={styles.message}>
          Loading users...
        </Text>
      </View>
    );
  }

  // error screen
  if (error) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>
          {error}
        </Text>
      </View>
    );
  }

  // list screen
  if (selectedUser === null) {
    return (
      <SafeAreaView style={styles.container}>
        <Text style={styles.title}>
          Users
        </Text>

        <FlatList
          data={users}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <UserItem
              user={item}
              onPress={() => setSelectedUser(item)}
            />
          )}
        />
      </SafeAreaView>
    );
  }

  // details screen
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.details}>

        <Pressable
          style={styles.backButton}
          onPress={() => setSelectedUser(null)}
        >
          <Text style={styles.backButtonText}>
            Back to Users
          </Text>
        </Pressable>

        <Text style={styles.title}>
          User Details
        </Text>

        {/* basic info */}
        <Text style={styles.detailLabel}>ID</Text>
        <Text style={styles.detailValue}>
          {selectedUser.id}
        </Text>

        <Text style={styles.detailLabel}>Name</Text>
        <Text style={styles.detailValue}>
          {selectedUser.name}
        </Text>

        <Text style={styles.detailLabel}>Username</Text>
        <Text style={styles.detailValue}>
          {selectedUser.username}
        </Text>

        <Text style={styles.detailLabel}>Email</Text>
        <Text style={styles.detailValue}>
          {selectedUser.email}
        </Text>

        <Text style={styles.detailLabel}>Phone</Text>
        <Text style={styles.detailValue}>
          {selectedUser.phone}
        </Text>

        <Text style={styles.detailLabel}>Website</Text>
        <Text style={styles.detailValue}>
          {selectedUser.website}
        </Text>

        {/* nested address */}
        <Text style={styles.sectionTitle}>
          Address
        </Text>

        <Text style={styles.detailValue}>
          {selectedUser.address.street},
          {' '}{selectedUser.address.suite}
        </Text>

        <Text style={styles.detailValue}>
          {selectedUser.address.city},
          {' '}{selectedUser.address.zipcode}
        </Text>

        <Text style={styles.detailLabel}>
          Coordinates
        </Text>

        <Text style={styles.detailValue}>
          Lat: {selectedUser.address.geo.lat}
          {'  '}
          Lng: {selectedUser.address.geo.lng}
        </Text>

        {/* nested company */}
        <Text style={styles.sectionTitle}>
          Company
        </Text>

        <Text style={styles.detailLabel}>
          Company Name
        </Text>

        <Text style={styles.detailValue}>
          {selectedUser.company.name}
        </Text>

        <Text style={styles.detailLabel}>
          Catch Phrase
        </Text>

        <Text style={styles.detailValue}>
          {selectedUser.company.catchPhrase}
        </Text>

        <Text style={styles.detailLabel}>
          Business
        </Text>

        <Text style={styles.detailValue}>
          {selectedUser.company.bs}
        </Text>

      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F4F6F8',
    padding: 16,
    // keep content below the Android status bar so the Back button can be tapped
    paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight + 16 : 16,
  },

  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },

  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 16,
  },

  userItem: {
    backgroundColor: 'white',
    padding: 16,
    marginBottom: 10,
    borderRadius: 10,
  },

  userName: {
    fontSize: 18,
    fontWeight: 'bold',
  },

  userEmail: {
    fontSize: 14,
    marginTop: 4,
  },

  message: {
    marginTop: 10,
    fontSize: 16,
  },

  errorText: {
    fontSize: 18,
    color: '#B00020',
  },

  details: {
    paddingBottom: 30,
  },

  backButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#1D4ED8',
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderRadius: 8,
    marginBottom: 18,
  },

  backButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },

  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginTop: 20,
    marginBottom: 8,
  },

  detailLabel: {
    fontSize: 13,
    fontWeight: 'bold',
    marginTop: 10,
  },

  detailValue: {
    fontSize: 16,
    marginTop: 2,
  },
});
